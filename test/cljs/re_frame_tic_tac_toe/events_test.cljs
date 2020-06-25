(ns re-frame-tic-tac-toe.events-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [re-frame-tic-tac-toe.db :as db]
            [re-frame-tic-tac-toe.effects :as effects]
            [re-frame-tic-tac-toe.events :as events]))

(deftest on-new-game-test
  (testing "returns the expected db, dispatches a close-modal event, and logs (effect)"
    (let [app-db (-> {}
                     (events/on-initialize-db [::events/initialize-db]))
          expected {:db app-db
                    :dispatch [::events/close-modal]
                    ::effects/log "NEW GAME"}
          cofx {:db app-db}]
      (is (= expected (events/on-new-game cofx))))))

(deftest on-place-mark-test
  (testing "updates the board, switches player, increments turn and adds the move to history"
    (let [n 9
          app-db (db/new-db n)
          cofx {:db app-db}
          i-cell 8
          e [::events/place-mark i-cell]
          next-board (assoc (:board app-db) i-cell :x)
          next-db (assoc app-db :board next-board :history [[i-cell :x]] :player :o :turn 2)
          expected {:db next-db}]
      (is (= expected (events/on-place-mark cofx e)))))
  (testing "dispatches a :game-over event for a winning move"
    (let [board {0 :x  1 :o  2 nil
                 3 nil 4 :x  5 :o
                 6 nil 7 nil 8 nil}
          turn 5
          app-db (assoc (db/new-db 9) :board board :turn turn)
          cofx {:db app-db}
          e [::events/place-mark 8]
          effects (events/on-place-mark cofx e)]
      (contains? effects :dispatch)
      (is (= [::events/game-over :x turn] (:dispatch effects))))))

(deftest on-game-over-test
  (let [winning-board-for-x {0 :x 4 :x 8 :x 2 :o 3 :o}
        app-db (assoc (db/new-db 9) :board winning-board-for-x)
        player :x
        turn 5
        cofx {:db app-db}
        e [::events/game-over player turn]
        effects (events/on-game-over cofx e)
        next-db (:db effects)]
    (testing "updates the game's status"
      (is (= :game-over (:status next-db))))
    (testing "shows a modal"
      (is (not (nil? (:modal next-db)))))))
