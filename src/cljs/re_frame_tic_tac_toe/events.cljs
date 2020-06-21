(ns re-frame-tic-tac-toe.events
  (:require
   [cljs.spec.alpha :as s]
   [clojure.set :refer [subset?]]
   [day8.re-frame.tracing :refer-macros [fn-traced]]
   [re-frame.core :as rf]
   [re-frame-tic-tac-toe.config :refer [debug?]]
   [re-frame-tic-tac-toe.db :as db]))

(defn- check-spec-and-throw
  "Throws an exception if `db` doesn't match the spec `a-spec`."
  [a-spec db]
  (when-not (s/valid? a-spec db)
    (throw (ex-info (str "spec check failed: " (s/explain-str a-spec db)) {}))))

(def check-db-spec-interceptor
  "Interceptor that runs after an event handler and checks whether the app-db
  schema is valid against a spec."
  (rf/after (partial check-spec-and-throw :re-frame-tic-tac-toe.db/db)))

;; (def winning-conditions-interceptor
;;   "TODO: add docs for this Interceptor."
;;   (rf/->interceptor
;;    :id :winning-conditions-interceptor
;;    :after
;;    (fn winning-conditions-after [context]
;;      (let [event (rf/get-coeffect context :event)
;;            old-db (rf/get-coeffect context :db)
;;            db (rf/get-effect context :db ::not-found)
;;            old-board-size (get-in old-db [:board :size])
;;            board-size (get-in db [:board :size])]
;;        (when (not= old-board-size board-size)
;;          (println "TODO: RECOMPUTE WINNING CELL COLLECTIONS"))
;;       ;;  (rf/assoc-coeffect context :db db)
;;          context))))

(def game-interceptors
  "Interceptor chain for the Tic Tac Toe game."
  [check-db-spec-interceptor
   (when debug? rf/debug)])

(rf/reg-event-db
 ::initialize-db
 game-interceptors
 (fn-traced [_ _]
   db/default-db))

(comment
  (rf/reg-event-db
   ::start-over
   (fn [_ _]
     db/default-db)))

(rf/reg-event-fx
 ::start-over
 (fn [cofx event]
   (println "::start-over" "cofx" cofx "event" event)
   {:db db/default-db}))

(defn filter-by-val
  [pred m]
  (into {} (filter (fn [[_ v]]
                     (pred v)) m)))

(defn make-pred
  "Build a predicate to check whether any of the winning collections of sets
  (e.g. #{:0-0, :1-1, :2-2} in a 3x3 game board) is a subset of the symbols
  placed by the player."
  [player-symbols-set]
  (fn [winning-set]
    (subset? winning-set player-symbols-set)))

;; TODO: coeffect for the alert? And maybe replace it with a modal.
(rf/reg-event-db
 ::place-symbol
 game-interceptors
 (fn [{:keys [board player turn] :as db} [_ cell winning-collections-sets]]
   (let [k (keyword cell)
         sym (k board)]
     (if sym
       (do
         (js/alert (str "Hey! You can't place a symbol on an occupied cell"))
         db)
       (let [[sym next-player] (if (= :x player) [:x :o] [:o :x])
             next-board (merge board {k sym})
             player-symbols (set (keys (filter-by-val #(= player %) next-board)))
             player-won? (some (make-pred player-symbols) winning-collections-sets)]
         (when player-won?
           (js/alert (str "Player " player " won!")))
         (assoc db :board next-board :player next-player :turn (+ 1 turn)))))))

(rf/reg-event-db
 ::change-board-size
 game-interceptors
 (fn [_ [_ size]]
   (merge db/default-db {:board {:size size}})))
