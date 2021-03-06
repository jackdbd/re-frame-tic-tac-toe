(ns re-frame-tic-tac-toe.views
  (:require
   [re-frame.core :as rf]
   [re-frame-tic-tac-toe.events :as events]
  ;;  [re-frame-tic-tac-toe.message :as msg]
   [re-frame-tic-tac-toe.modal :refer [modal-panel]]
   [re-frame-tic-tac-toe.subs :refer [<sub] :as subs]
   [re-frame-tic-tac-toe.views.marks :refer [o-mark x-mark]]
   [re-frame-tic-tac-toe.views.tracks :refer [tracks]]))

;; (defonce worker (js/Worker. "js/compiled/worker.js"))

;; (defn message-handler [e]
;;   (let [action (.. e -data -action)]
;;     (case action
;;       "notify-main" (println "From worker: " (.. e -data -payload))
;;       "error" (println "ERROR " (.. e -data))
;;       :else (println "Unhandled message" e))))

;; (. worker (addEventListener "message" message-handler))

(defn header []
  (let [player-label @(rf/subscribe [::subs/player-label])
        turn @(rf/subscribe [::subs/turn])]
    [:header
     player-label "'s turn" " (turn " turn ")"]))

(defn cell-rect
  "Rectangular clickable area for the cell."
  [{:keys [data-index size-px]}]
  (let [css-class @(rf/subscribe [::subs/player-class])]
    [:rect.cell-clickable {:class css-class
                           :x 0 :y 0 :width size-px :height size-px
                           :data-index data-index}]))

(defn cell [{:keys [col row mark side size-px]}]
  (let [x (* col size-px)
        y (* row size-px)
        data-index (+ (* side row) col)]
    [:g.cell {:transform (str "translate(" x "," y ")")}
     [cell-rect {:data-index data-index :size-px size-px}]
     (cond
       (= :x mark) [x-mark {:padding 5 :size-px size-px :data-index data-index}]
       (= :o mark) [o-mark {:padding 5 :size-px size-px :data-index data-index}]
       :else nil)]))

(defn make-coords->cell
  [board side cell-px]
  (fn coords->cell
    [i [row col]]
    (let [mark (get board i)
          key (str row "-" col " (side " side "; cell-px " cell-px ")")]
      ^{:key key} [cell {:col col :row row :side side :size-px cell-px :mark mark}])))

(defn cells
  [side cell-px]
  [:g.cells
   (let [board @(rf/subscribe [::subs/board])
         cell-coords @(rf/subscribe [::subs/cell-coords])
         coords->cell (make-coords->cell board side cell-px)]
     (map-indexed coords->cell cell-coords))])

(defn game-board
  []
  (let [side (<sub [::subs/board-side])
        cell-side-px 200
        side-px (* side cell-side-px)]
    [:svg.board {:width side-px :height side-px
                 :on-click (fn [event]
                            ;;  (. worker (postMessage (msg/message "notify" "Hello from main")))
                             (when-let [idx (.. event -target -dataset -index)]
                               (rf/dispatch [::events/click-on-cell idx])))}
     [tracks side-px cell-side-px]
     [cells side cell-side-px]]))

(defn board-size-selector
  []
  (let [name "select-board-size"]
    [:<>
     [:label {:for name} "Select the board size"]
     [:select {:id name :name name
               :on-change #(rf/dispatch [::events/change-board (-> % .-target .-value)])}
      [:option {:value "9"} "3x3"]
      [:option {:value "16"} "4x4"]
      [:option {:value "25"} "5x5"]]]))

(defn footer []
  [:footer
   [:button {:type "button" :on-click #(rf/dispatch [::events/new-game])}
    "Click to start over"]
   [board-size-selector]])

(defn modal-window
  []
  (let [child @(rf/subscribe [::subs/modal])]
    (when child
      [modal-panel {:child [child]
                    :on-dismiss #(rf/dispatch [::events/close-modal])
                    :size :small}])))

(defn app []
  [:<>
   [modal-window]
   [:div.centered
    [:div.column
     [header]
     [game-board]
     [footer]]]])
