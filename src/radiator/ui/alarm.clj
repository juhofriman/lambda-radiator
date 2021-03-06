(ns radiator.ui.alarm
  (:require [radiator.ui.images :as img]
            [clj-time.format :as f]
            [radiator.source.common :as common]))

(defn alarms-box
  [alarms alarms-history]
  (when alarms
    [:div.light-grey-background
     (if (common/has-alarms? alarms)
       [:div
        [:div.warning-background
         [:div.t-b-5px-padding {:align "center"}
          img/alarm-30px
          [:span.header.text-center "Alarms"]]]
        (for [alarm (common/filter-ok-alarms alarms)]
          [:div.t-b-5px-padding img/alarm-30px [:span (:name alarm)]])]
       [:div
        [:div.ok-background
         [:div.t-b-5px-padding {:align "center"}
          img/ok-30px
          [:span.header.text-center "Alarms"]]]
        [:div.t-b-5px-padding img/ok-30px [:span (str "All " (count alarms) " OK")]]])
     (when-not (empty? alarms-history)
       [:div
        [:h2 "Alarms in last 24H"]
       (for [alarm alarms-history]
         [:div.t-b-5px-padding img/alarm-30px [:span (:name alarm) " " (f/unparse (f/formatters :hour-minute)(:timestamp alarm)) " (UTC)"]])])]))


