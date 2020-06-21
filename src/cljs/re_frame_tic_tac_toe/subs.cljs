(ns re-frame-tic-tac-toe.subs
  (:require
   [re-frame.core :as rf]))

(rf/reg-sub
 ::player
 (fn [db]
   (:player db)))

(rf/reg-sub
 ::turn
 (fn [db]
   (:turn db)))
