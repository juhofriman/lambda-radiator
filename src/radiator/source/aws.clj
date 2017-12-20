(ns radiator.source.aws
  (:require [clj-http.client :as client]
            [clojure.data.json :as json]))

(defn- round [s n]
  (.setScale (bigdec n) s java.math.RoundingMode/HALF_EVEN))

;; Pipelines

(defn transform-pipeline
  [{:keys [currentStatus name]}]
  (condp = currentStatus
    "Succeeded" {:name   name
                 :pipeline-status :success}
    "Failed"    {:name   name
                 :pipeline-status :failed}
    "InProgress" {:name   name
                  :pipeline-status :in-progress}
    {:name name
     :pipeline-status :unknown}))

(defn transform-pipelines
  [pipelines]
  (map transform-pipeline pipelines))

;; Alarms

(defn transform-alarm
  [{:keys [AlarmName StateValue]}]
  (condp = StateValue
    "OK"  {:name   AlarmName
           :alarm-status :ok}
    {:name   AlarmName
     :alarm-status :alarm}))

(defn transform-alarms
  [alarms]
  (map transform-alarm alarms))

;; Metrics

(defn transform-metric
  [{:keys [name unit result]}]
  {:name name
   :metric-value (condp = unit
            "Percent" (str (round 3 result) " %")
            result)})

(defn transform-metrics
  [metrics]
  (map transform-metric metrics))

(defn pipeline-and-alarm-statuses
  [{:keys [uri api-key]}]
  (try
    (json/read-str (:body (client/get uri
                                      {:socket-timeout 2000 :conn-timeout 2000
                                       :headers {"x-api-key" api-key}})) :key-fn keyword)
    (catch Exception e
      (do
        (clojure.stacktrace/print-stack-trace e)
        nil))))