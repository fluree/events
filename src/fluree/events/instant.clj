(ns fluree.events.instant
  (:import (java.time Instant)))

(defn before?
  "Returns true if instant is before provided comparison instant"
  [^Instant instant ^Instant compare]
  (.isBefore instant compare))

(defn after?
  "Returns true if instant is after provided comparison instant"
  [^Instant instant ^Instant compare]
  (.isAfter instant compare))

(defn equals?
  "Returns true if instant is after provided comparison instant"
  [^Instant instant ^Instant compare]
  (.equals instant compare))

(defn now
  "Current time instant"
  []
  (Instant/now))

