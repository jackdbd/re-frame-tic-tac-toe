(ns re-frame-tic-tac-toe.effects
  (:require
   [re-frame.core :as rf]))

(rf/reg-fx
 ::log
 (fn [value]
   (.warn js/console value)))
