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

(rf/reg-sub
 ::board-side
 (fn [db]
   (.sqrt js/Math (count (:board db)))))

(rf/reg-sub
 ::cells-total
 (fn [db]
   (count (:board db))))

(rf/reg-sub
 ::cell-coords
 (fn [_ _]
   [(rf/subscribe [::cells-total])
    (rf/subscribe [::board-side])])
 (fn [[n side] _]
   (let [i->coords (fn [i] [(quot i side) (mod i side)])]
     (map i->coords (range n)))))

(def <sub
  "Alternative to avoid having to deref the returned value in a view.
   usage: foo (<sub [::subs/foo])
   https://github.com/day8/re-frame/blob/04433cfa60a8c8116e2b6aefd8fd253014285229/src/re_frame/subs.cljc#L106"
  (comp deref rf/subscribe))


