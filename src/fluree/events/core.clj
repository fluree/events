(ns fluree.events.core
  (:gen-class)
  (:require [fluree.db.api :as fdb]
            [clj-cron-parse.core :refer [next-date]]
            [clj-time.core :as t]
            [clojure.core.async :as async :refer [go-loop]]
            [clojure.string :as str]
            [clojure.walk :refer [keywordize-keys]]
            [clojure.tools.logging :as log]
            [cheshire.core :as json]
            [aleph.http :as xhttp]))

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
  (let [{:keys [eventMonitor/cron]} monitor-map
        next-run (next-date ref-time cron)]
    ;; if next-run is -not- after now return true (run)
    (not (t/after? next-run now))))


(defn query-change?
  [db monitor]
  (log/info "query-change? monitor: " (pr-str monitor))
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
        (log/info "Difference triggered for:" (:eventMonitor/id monitor) (:eventMonitor/name monitor))
        @(xhttp/get "https://l92xqqxncf.execute-api.us-east-1.amazonaws.com/Prod/hello")
        (swap! system assoc-in [:block (:_id monitor)] block)
        true)
      (do
        false))))


(defn start-loop
  []
  (async/go
    (try
      (loop [ref-time (t/now)]                              ;; last time utilized for check
        (when-not (:closed? @system)
          (let [next           (async/<! (async/timeout loop-timeout-ms))
                now            (t/now)
                db             (fdb/db (:conn @system) (:ledger @system))
                monitorRecords (monitor-records db)
                monitors       (->> monitorRecords
                                    (filter #(to-invoke % ref-time now)))]
            (doseq [monitor monitors]
              (query-change? db monitor))
            (recur now))))
      (catch Exception e
        (log/error "Caught fatal exception: " (.getMessage e))
        (throw e)))
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


  (def now (t/now))
  now
  (next-date now "*/15 * * * * *")
  (not (t/after? (next-date now "*/15 * * * * *")
                 (t/now)))


  (next-date now "5 * * * * *")

  (async/<!! (xhttp/get-json "https://l92xqqxncf.execute-api.us-east-1.amazonaws.com/Prod/hello" {:body {:token "BLAH"}}))

  )
