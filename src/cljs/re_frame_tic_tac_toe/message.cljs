(ns re-frame-tic-tac-toe.message
  (:require [cljs.spec.alpha :as s]))

(s/def ::action string?)
(s/def ::payload (s/or :string string? :integer number?))
(s/def ::data (s/keys :req-un [::action ::payload]))
(s/def ::event (s/keys :req-un [::data]))

(defn message [action value]
  (clj->js {:action action :payload {:value value}}))
