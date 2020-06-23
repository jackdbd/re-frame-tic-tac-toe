(ns re-frame-tic-tac-toe.events
  (:require
   [day8.re-frame.tracing :refer-macros [fn-traced]]
   [re-frame.core :as rf]
   [re-frame-tic-tac-toe.db :as db]
   [re-frame-tic-tac-toe.effects :as effects]
   [re-frame-tic-tac-toe.logic :refer [make-winning-subset? winning-sets]]
   [re-frame-tic-tac-toe.utils :refer [check-spec-and-throw filter-by-val]]))

(def check-db-spec-interceptor
  "Interceptor that runs after an event handler and checks whether the app-db
  schema is valid against a spec."
  (rf/after (partial check-spec-and-throw :re-frame-tic-tac-toe.db/db)))

(def game-interceptors
  "Interceptor chain for the Tic Tac Toe game."
  [check-db-spec-interceptor])

(rf/reg-event-db
 ::initialize-db
 game-interceptors
 (fn-traced [_ _]
   db/default-db))

;; Start a new game, keeping the selected board size.
(rf/reg-event-db
 ::start-over
 (fn [db _]
   (db/new-db (count (:board db)))))

(rf/reg-event-fx
 ::game-over
 game-interceptors
 (fn [{:keys [db]} [_ message]]
   {:db (assoc db :status :game-over) ::effects/alert message}))

;; A cell is marked with the current player's mark (i.e :x or :o). With this
;; move we also check if the current player won the game.
(rf/reg-event-fx
 ::place-mark
 game-interceptors
 (fn [{:keys [db]} [_ k]]
   (let [player (:player db)
         next-player (if (= :x player) :o :x)
         next-board (assoc (:board db) k player)
         player-marks-set (set (keys (filter-by-val #(= player %) next-board)))
         winning-subset? (make-winning-subset? player-marks-set)
         player-won? (some winning-subset? (winning-sets (count (:board db))))]
     ;; whether the player won or not, we always want to draw his mark on the
     ;; board. I think that failing to do so would be confusing.
     ;; No need to switch player or turn if the current player won. It's game over.
     ;; TODO: dispatch a ::start-over when the player dismisses a modal
     (if player-won?
       {:db (assoc db :board next-board)
        :dispatch-later [{:ms 100 :dispatch [::game-over (str "Player " player " won!")]}
                         {:ms 500 :dispatch [::start-over]}]}
       {:db (assoc db :board next-board :player next-player :turn (+ 1 (:turn db)))}))))

;; A player clicked on a cell.
(rf/reg-event-fx
 ::click-on-cell
 game-interceptors
 (fn handle-click-on-cell [{:keys [db]} [_ idx]]
   (let [k (keyword idx)] ;; idx is a string representing an index
     (if-not (nil? (get-in db [:board k]))
       {:db db ::effects/alert (str "Hey! You can't place a symbol on an occupied cell")}
       {:dispatch [::place-mark k]}))))

;; A player selects a new board size.
(rf/reg-event-db
 ::change-board
 game-interceptors
 (fn [_ [_ n-as-str]]
   (merge db/default-db {:board (db/new-board (js/parseInt n-as-str 10))})))
