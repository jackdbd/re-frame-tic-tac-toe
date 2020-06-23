(ns re-frame-tic-tac-toe.views
  (:require
   [re-frame.core :as rf]
   [re-frame-tic-tac-toe.subs :refer [<sub] :as subs]
   [re-frame-tic-tac-toe.events :as events]
   ))

(defn header []
  (let [player-label @(rf/subscribe [::subs/player-label])
        turn @(rf/subscribe [::subs/turn])]
    [:header
     player-label "'s turn" " (turn " turn ")"]))

(defn x-symbol
  "Symbol for player X."
  [{:keys [data-coords padding size]}]
  [:<>
   [:line.x-symbol {:x1 padding :y1 padding
                    :x2 (- size padding) :y2 (- size padding)
                    :data-coords data-coords}]
   [:line.x-symbol {:x1 padding :y1 (- size padding)
                    :x2 (- size padding) :y2 padding
                    :data-coords data-coords}]])

(defn o-symbol
  "Symbol for player O."
  [{:keys [data-coords padding size]}]
  (let [r (- (/ size 2) padding)
        cx (+ r padding)
        cy cx]
    [:circle.o-symbol {:cx cx :cy cy :r r
                       :data-coords data-coords}]))

(defn cell-frame
  "Rectangular frame for the cell.
  Important: since we want the <rect> to be invisible, we use fill:none in CSS,
  but this would prevent mouse events from working properly on the element. We
  fix this issue by setting pointer-events to `visible`.
  https://stackoverflow.com/questions/12443309/svg-detect-onclick-events-on-fill-none
  https://www.w3.org/TR/SVG/interact.html#PointerEventsProperty"
  [{:keys [data-coords size]}]
  (let [css-class @(rf/subscribe [::subs/player-class])]
    [:rect.cell-frame {:class css-class
                       :x 0 :y 0 :width size :height size
                       :pointer-events "visible" :data-coords data-coords}]))

;; TODO: does this really have to re-run every timeany other cell on the board
;; changes? Maybe use a query for the subscription?
(defn cell [{:keys [column key row size]}]
  (let [x (* column size)
        y (* row size)
        data-coords (str row "-" column)
        board @(rf/subscribe [::subs/board])
        symbol (key board)]
    [:g.cell {:transform (str "translate(" x "," y ")")}
     [cell-frame {:size size :data-coords data-coords}]
     (cond
       (= :x symbol) [x-symbol {:padding 5 :size size :data-coords data-coords}]
       (= :o symbol) [o-symbol {:padding 5 :size size :data-coords data-coords}]
       :else nil)]))

(defn tracks
  [size-px cell-px]
  [:g.tracks
   (for [x (range cell-px size-px cell-px)]
     ^{:key (str "track-hor-" x)} [:line.track {:x1 x :y1 0 :x2 x :y2 size-px}])
   (for [y (range cell-px size-px cell-px)]
     ^{:key (str "track-ver-" y)} [:line.track {:x1 0 :y1 y :x2 size-px :y2 y}])])

;; TODO: I suspect dispatching an event with the winning-collections-sets (huge)
;; is a really bad idea, performance-wise. Think about alternative solutions.
;; Maybe use a coeffect? Can I use an interceptor?
(defn board
  "The game board."
  []
  (let [winning-collections-sets @(rf/subscribe [::subs/winning-collections-sets])
        size (<sub [::subs/board-size])
        cell-size-px 80
        size-px (* size cell-size-px)]
    [:svg.board {:width size-px :height size-px
                 :on-click (fn [event]
                             (when-let [coords (-> event .-target .-dataset .-coords)]
                               (rf/dispatch [::events/place-symbol coords winning-collections-sets])))}
     [tracks size-px cell-size-px]
     [:g.cells
      (for [r (range size) c (range size)]
        (let [k (keyword (str r "-" c))]
          ^{:key k} [cell {:column c :key k :row r :size cell-size-px}]))]]))

(defn board-size-selector
  []
  (let [name "select-board-size"]
    [:<>
     [:label {:for name} "Select the board size"]
     [:select {:id name :name name
               :on-change #(rf/dispatch [::events/change-board-size (-> % .-target .-value (js/parseInt 10))])}
      [:option {:value 3} 3]
      [:option {:value 4} 4]
      [:option {:value 5} 5]]]
    ))

(defn footer []
  [:footer
   [:button {:type "button" :on-click #(rf/dispatch [::events/start-over])}
    "Click to start over"]
   [board-size-selector]])

;; TODO: add history component with a filter to show moves of #{:player-x :player-o :all}

(defn app []
  [:<>
   [header]
   [board]
   [footer]])
