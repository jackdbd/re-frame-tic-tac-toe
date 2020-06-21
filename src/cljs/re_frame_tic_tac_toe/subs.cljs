(ns re-frame-tic-tac-toe.subs
  (:require
   [re-frame.core :as rf]))

(rf/reg-sub
 ::player
 (fn [db _]
   (:player db)))

(rf/reg-sub
 ::player-label
 (fn [_ _]
   [(rf/subscribe [::player])])
 (fn [[player] _]
   (if (= :x player)
     "Player X"
     "Player O")))

(rf/reg-sub
 ::player-class
 (fn [_ _]
   [(rf/subscribe [::player])])
 (fn [[player] _]
   (if (= :x player)
     "player-x"
     "player-o")))

(rf/reg-sub
 ::turn
 (fn [db]
   (:turn db)))

(rf/reg-sub
 ::board
 (fn [db]
   (:board db)))
