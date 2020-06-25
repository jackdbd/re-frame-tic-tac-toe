(ns re-frame-tic-tac-toe.modal
  (:require
   [re-frame.core :as rf]
   [re-frame-tic-tac-toe.subs :as subs]
   [re-frame-tic-tac-toe.utils :refer [make-i->coords]]
   [re-frame-tic-tac-toe.views.marks :refer [o-mark x-mark]]
   [re-frame-tic-tac-toe.views.tracks :refer [tracks]]))

;; TODO call on-dismiss on ESC (on-keydown seems not to work)

;; TODO: avoid duplicates for tracks, x-mark, o-mark, cell

;; TODO: think about a better solution for the modals

(defn cell [{:keys [col row mark size-px]}]
  (let [x (* col size-px)
        y (* row size-px)]
    [:g.cell {:transform (str "translate(" x "," y ")")}
     (cond
       (= :x mark) [x-mark {:padding 5 :size-px size-px}]
       (= :o mark) [o-mark {:padding 5 :size-px size-px}]
       :else nil)]))

(defn make-history-entry->cell
  [side cell-px]
  (fn history-entry->cell
    [[i mark]]
    (let [i->coords (make-i->coords side)
          [row col] (i->coords i)
          k (str mark "-on-cell-" i)]
      ^{:key k} [cell {:col col :row row :side side :size-px cell-px :mark mark}])))

(defn replayed-moves
  [side cell-px]
  [:g.cells
   (let [moves @(rf/subscribe [::subs/replayed-moves])
         f (make-history-entry->cell side cell-px)]
     (map f moves))])

(defn replay-game-board
  []
  (let [side @(rf/subscribe [::subs/board-side])
        cell-side-px 100
        side-px (* side cell-side-px)]
    [:svg.board {:width side-px :height side-px}
     [tracks side-px cell-side-px]
     [replayed-moves side cell-side-px]]))

(defn make-modal-child-game-over
  "High order function that returns a modal-child-game-over component."
  [{:keys [description ok on-click title] :or {ok "Ok" title "" description ""}}]
  (fn modal-child-game-over
    []
    [:div
     {:style {:background-color "white"
              :padding          "16px"
              :border-radius    "6px"
              :text-align "center"}}
     [:h3 title]
     [:p description]
     [:h5 "Replay"]
     [:div.replay
      [replay-game-board]]
     [:button {:type :button :on-click on-click} ok]]))

(defn make-modal-child-invalid-move
  "High order function that returns a modal-child-invalid-move component."
  [{:keys [description ok on-click title] :or {ok "Ok" title "" description ""}}]
  (fn modal-child-invalid-move
    []
    [:div
     {:style {:background-color "white"
              :padding          "16px"
              :border-radius    "6px"
              :text-align "center"}}
     [:h3 title]
     [:p description]
     [:button {:type :button :on-click on-click} ok]]))

(defn modal-panel
  "TODO: docs"
  [{:keys [child on-dismiss size]}]
  [:div.modal-wrapper
   [:div.modal-backdrop {:on-click on-dismiss}]
   [:div.modal-child {:style {:width (case size
                                       :extra-small "15%"
                                       :small "30%"
                                       :large "70%"
                                       :extra-large "85%"
                                       "50%")}} child]])
