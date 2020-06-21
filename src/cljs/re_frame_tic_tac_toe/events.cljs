(ns re-frame-tic-tac-toe.events
  (:require
   [re-frame.core :as rf]
   [re-frame-tic-tac-toe.db :as db]
   [day8.re-frame.tracing :refer-macros [fn-traced]]
   ))

(rf/reg-event-db
 ::initialize-db
 (fn-traced [_ _]
   db/default-db))

;; (rf/reg-event-db
;;  ::next-turn
;;  [(rf/path :turn)]
;;  (fn [old-value [event new-value]]
;;    (println "old" old-value "new" new-value "event" event)
;;    new-value))

(rf/reg-event-db
 ::next-turn
 (fn [{:keys [player turn] :as db} _]
   (let [next-player (if (= :player-a player)
                       :player-b
                       :player-a)]
     (assoc db :turn (+ 1 turn) :player next-player))))

 (rf/reg-event-db
  ::start-again
  (fn [_ _]
    db/default-db))

;; (rf/reg-event-fx
;;  ::start-again
;;  (fn [cofx event]
;;    (println "=== cofx ===" cofx event)
;;    {:db db/default-db}))
