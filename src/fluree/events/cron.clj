(ns fluree.events.cron
  (:import (java.time ZonedDateTime ZoneId Instant)
           (com.cronutils.model.definition CronDefinitionBuilder)
           (com.cronutils.model CronType Cron)
           (com.cronutils.parser CronParser)
           (java.util Locale)
           (com.cronutils.descriptor CronDescriptor)
           (com.cronutils.model.time ExecutionTime)))

;; We use quartz-style cron expressions - 6 or 7 elements including seconds (first) and optionally year (7th)
;; see table below for more info
(def quartz-cron-def (CronDefinitionBuilder/instanceDefinitionFor CronType/QUARTZ))

;DEFINITION   CRONTAB     QUARTZ      JODATIME
;Day of Year    -             -         1-366
;Year           -         1970-2099   +/- 290.000.000
;Day of Week  0-6, SUN=0  1-7, SUN=1  1-7, SUN=7
;Month	        1-12	      1-12	      1-12
;Day of Month   1-31        1-31        1-31
;Hours          0-23        0-23        0-23
;Minutes        0-59        0-59        0-59
;Seconds        -           0-59        0-59

(defn parse
  "Parses a cron string into a com.cronutils.model.Cron class.
  Single arity uses quartz cron format as default."
  ([cron-str] (parse cron-str quartz-cron-def))
  ([cron-str cron-def]
   (-> cron-def
       CronParser.
       (.parse cron-str))))

(defn to-cron
  "Coerces value to com.cronutils.model.Cron class."
  [cron]
  (if (string? cron)
    (parse cron)
    cron))

(defn as-string
  "Parses a cron instance and returns a string"
  [^Cron cron]
  (.asString cron))

(defn describe
  "Outputs string description of cron definition, or cron string."
  ([cron]
   (describe cron (Locale/getDefault)))
  ([cron locale]
   (->> (to-cron cron)
        (.describe (CronDescriptor/instance locale)))))

(defn- instant->zoned-date-time
  "Converts a java.time.Instant to java.time.ZonedDateTime"
  [^Instant instant]
  (.atZone instant (ZoneId/of "Z")))

(defn- zoned-date-time->instant
  "Converts a java.time.ZonedDateTime to java.time.Instant"
  [^ZonedDateTime zoned-date-time]
  (.toInstant zoned-date-time))

(defn next-execution
  "Returns next execution time as a java.time.Instant from provided instant.
  Single-arity defaults instant to now."
  ([cron] (next-execution cron (Instant/now)))
  ([cron ^Instant instant]
   (-> (to-cron cron)
       ExecutionTime/forCron
       (.nextExecution (instant->zoned-date-time instant))
       .get
       zoned-date-time->instant)))

(defn last-execution
  "Returns next execution time as a java.time.Instant from provided instant.
  Single-arity defaults instant to now."
  ([cron] (last-execution cron (Instant/now)))
  ([cron ^Instant instant]
   (-> (to-cron cron)
       ExecutionTime/forCron
       (.lastExecution (instant->zoned-date-time instant))
       .get
       zoned-date-time->instant)))

