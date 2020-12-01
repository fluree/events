(ns fluree.events.core
  (:gen-class)
  (:require [fluree.db.api :as fdb]
            [clojure.core.async :as async :refer [go-loop]]
            [clojure.walk :refer [keywordize-keys]]
            [clojure.tools.logging :as log]
            [cheshire.core :as json]
            [aleph.http :as xhttp]
            [fluree.events.instant :as inst]
            [fluree.events.cron :as cron]))

(def system (atom {}))
(def loop-timeout-ms 1000)


(defn latest-block
  [db]
  (:block (async/<!! db)))


(defn close []
  (swap! system assoc :closed? true)
  (fdb/close (:conn @system))
  :done)

(defn monitor-records
  [db]
  (let [query   {:select [:*] :from "eventMonitor"}
        results @(fdb/query db query)]
    (when (instance? Exception results)
      (throw results))
    (->> results
         (map keywordize-keys))))


(defn to-invoke
  "Filters schedules such that only ones that should be run are left."
  [monitor-map ref-time now]
  (try
    (let [{:keys [eventMonitor/cron]} monitor-map
          next-run (cron/next-execution cron ref-time)]
      ;; if next-run is -not- after now return true (run)
      (not (inst/after? next-run now)))
    (catch Exception e
      (log/error e (str "Unable to parse cron expression, skipping: " (:eventMonitor/cron monitor-map)
                        {:monitor-map monitor-map
                         :now         (str now)
                         :last-time   (str ref-time)}))
      false)))


(defn query-change?
  [db monitor]
  (log/debug "query-change? monitor: " (pr-str monitor))
  (let [block      (latest-block db)
        old-block  (or (get-in @system [:block (:_id monitor)])
                       (:start-block @system))
        ledger     (:ledger @system)
        auth       (get-in monitor [:eventMonitor/auth :_id])
        new-db     (fdb/db (:conn system) ledger {:block block
                                                  :auth  auth})
        old-db     (fdb/db (:conn system) ledger {:block old-block
                                                  :auth  auth})
        query      (-> monitor :eventMonitor/query (json/parse-string true))
        result     @(fdb/query new-db query)
        old-result @(fdb/query old-db query)]
    (if (not= result old-result)
      (do
        (log/info (str "Monitor triggered - last execution as-of: " old-block " this execution as-of: " block)
                  (json/encode monitor))
        (try @(xhttp/get (:eventMonitor/webhook monitor))
             (catch Exception e (log/error e (str "Error executing webhook: " (:eventMonitor/webhook monitor)
                                                  {:monitor monitor}))))
        (swap! system assoc-in [:block (:_id monitor)] block)
        true)
      (do
        false))))


(defn start-loop
  []
  (async/go
    (try
      (loop [ref-time (inst/now)]                           ;; last time utilized for check
        (when-not (:closed? @system)
          (let [next           (async/<! (async/timeout loop-timeout-ms))
                now            (inst/now)
                db             (fdb/db (:conn @system) (:ledger @system))
                monitorRecords (monitor-records db)
                monitors       (->> monitorRecords
                                    (filter #(to-invoke % ref-time now)))]
            (doseq [monitor monitors]
              (query-change? db monitor))
            (recur now))))
      (catch Exception e
        (log/error "Caught fatal exception, exiting: " (.getMessage e))
        (throw e)
        (System/exit 1)))
    (log/info "Closed!")
    (swap! system dissoc :closed?)
    :closed))

(defn startup [ledger-server ledger-name]
  (let [ledger-server (or ledger-server
                          (System/getenv "FLUREE_SERVER")
                          "http://localhost:8090")
        ledger-name   (or ledger-name
                          (System/getenv "FLUREE_LEDGER")
                          "daas/sprint6")
        _             (log/info (str "Starting events service connecting to server: " ledger-server " and ledger: " ledger-name))
        conn          (try (fdb/connect ledger-server)
                           (catch Exception e
                             (log/error e "Unable to create connection with error: " (.getMessage e))
                             (log/error "Shutting down!")
                             (System/exit 1)))
        db            (fdb/db conn ledger-name)]
    ;; try to make sure db resolves without an error before moving on
    (try (when-let [ex (instance? Exception (async/<!! db))]
           (throw ex))
         (catch Exception e
           (log/error "Unable to resolve ledger: " ledger-name ". Error: " (.getMessage e))
           (log/error "Shutting down!")
           (System/exit 1)))
    (swap! system assoc
           :conn conn
           :ledger ledger-name
           :start-block (latest-block db))
    ;; block to keep running until shutdown
    (async/<!! (start-loop))))


(defn -main [& args]
  (startup (System/getenv "FLUREE_SERVER") (System/getenv "FLUREE_LEDGER"))
  (.addShutdownHook
    (Runtime/getRuntime)
    (Thread. ^Runnable
             (fn []
               (log/info "Shutting down")
               (close)))))



(comment


  (startup "http://localhost:8080" "daas/sprint6")
  (close)

  (-> @system)


  )


(comment

  (def db-name "event/a1")

  (->> (monitor-records (fdb/db (:conn system) db-name))
       (map keywordize-keys))



  (async/<!! (xhttp/get-json "https://l92xqqxncf.execute-api.us-east-1.amazonaws.com/Prod/hello" {:body {:token "BLAH"}}))

  )
