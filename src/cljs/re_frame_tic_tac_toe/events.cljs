(ns re-frame-tic-tac-toe.events
  (:require
   [day8.re-frame.tracing :refer-macros [fn-traced]]
   [re-frame.core :as rf]
   [re-frame-tic-tac-toe.db :as db]
   [re-frame-tic-tac-toe.effects :as effects]
   [re-frame-tic-tac-toe.logic :refer [make-winning-subset? winning-sets]]
   [re-frame-tic-tac-toe.modal :refer [make-modal-child-game-over make-modal-child-invalid-move]]
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
 (fn [_ _]
   db/default-db))

;; A cell is marked with the current player's mark (i.e :x or :o). With this
;; move we also check if the current player won the game.
(rf/reg-event-fx
 ::place-mark
 game-interceptors
 (fn [{:keys [db] {:keys [board history player turn]} :db} [_ i]]
   (let [next-player (if (= :x player) :o :x)
         next-board (assoc board i player)
         next-history (conj history [i player])
         player-marks-set (set (keys (filter-by-val #(= player %) next-board)))
         winning-subset? (make-winning-subset? player-marks-set)
         player-won? (some winning-subset? (winning-sets (count board)))]
     ;; Whether the player won or not, we always want to draw his mark on the
     ;; board. I think that failing to do so would be confusing.
     ;; No need to switch player or turn if the current player won. It's game over.
     (if player-won?
       {:db (assoc db :board next-board :history next-history)
        :dispatch-later [{:ms 100 :dispatch [::game-over player turn]}]}
       {:db (assoc db :board next-board :history next-history :player next-player :turn (+ 1 turn))}))))

;; A player selects a new board size.
(rf/reg-event-db
 ::change-board
 game-interceptors
 (fn-traced [_ [_ n-as-str]]
            (merge db/default-db {:board (db/new-board (js/parseInt n-as-str 10))})))

(rf/reg-event-db
 ::close-modal
 game-interceptors
 (fn [db _]
   (assoc db :modal nil)))

(rf/reg-event-fx
 ::new-game
 game-interceptors
 (fn [{:keys [db]} _]
   {:db (db/new-db (count (:board db)))
    :dispatch [::close-modal]
    ::effects/log "NEW GAME"}))

; If the button "New Game" is clicked while the replay is still going on (i.e
; there are still some ::replay-move events in the re-frame event queue) we have
; a different history from the one we had at the time the event was scheduled.
; With a new game the history is empty, so we have no replayed moves to show.
(rf/reg-event-db
 ::replay-move
 game-interceptors
 (fn [{:keys [history replayed-moves] :as db} [_ i]]
   (if (nil? (get history i))
     db
     (assoc db :replayed-moves (conj replayed-moves (nth history i))))))

(defn i->dispatch-replay-move
  [i]
  {:ms (* i 500) :dispatch [::replay-move i]})

(rf/reg-event-fx
 ::game-over
 game-interceptors
 (fn [{:keys [db]} [_ player turn]]
   (let [m {:title "Game Over"
            :description (str "Player " player " won at turn " turn "!")
            :ok "New Game"
            :on-click #(rf/dispatch [::new-game])}
         modal-child (make-modal-child-game-over m)
         n (count (:history db))]
     {:db (assoc db :status :game-over :modal modal-child)
      :dispatch-later (map i->dispatch-replay-move (range n))})))

;; A player clicked on a cell.
(rf/reg-event-fx
 ::click-on-cell
 game-interceptors
 (fn handle-click-on-cell [{:keys [db]} [_ i-as-str]]
   (let [i (js/parseInt i-as-str 10)]
     (if-not (nil? (get-in db [:board i]))
       (let [m {:title "Invalid move"
                :description (str "You can't place a symbol on an occupied cell")
                :ok "Ok, Gotcha"
                :on-click #(rf/dispatch [::close-modal])}
             modal-child (make-modal-child-invalid-move m)]
         {:db (assoc db :modal modal-child)})
       {:dispatch [::place-mark i]}))))