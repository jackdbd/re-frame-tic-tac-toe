(ns re-frame-tic-tac-toe.effects
  (:require
   [re-frame.core :as rf]))

(rf/reg-fx
 ::alert
 (fn [value]
   (js/alert value)))
