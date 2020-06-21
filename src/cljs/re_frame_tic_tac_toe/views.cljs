(ns re-frame-tic-tac-toe.views
  (:require
   [re-frame.core :as rf]
   [re-frame-tic-tac-toe.subs :as subs]
   [re-frame-tic-tac-toe.events :as events]
   ))

(defn header []
  (let [player (rf/subscribe [::subs/player])
        player-label (if (= :player-a @player)
                       "Player A"
                       "Player B")
        turn (rf/subscribe [::subs/turn])]
    [:header {:on-click #(rf/dispatch [::events/next-turn])}
     "Now is " player-label "'s turn" " (Turn " @turn ")"]))

(defn cell [{:keys [column row size]}]
  [:rect.cell {:fill "lightgray" :stroke "orange"
               :x (* column size) :y (* row size) :width size :height size
               :data-cell (str row "-" column)}])

; (defn handle-click
;   [event]
;   (when-let [data-cell (-> event .-target .-dataset .-cell)]
;     (println "Clicked on cell" data-cell)))

(defn board
  "The game board."
  [{:keys [columns rows]}]
  (let [cell-size 50]
    [:svg.board {:width 500 :height 250
                 :on-click (fn [event]
                             (when-let [data-cell (-> event .-target .-dataset .-cell)]
                               (println "Clicked on cell" data-cell)
                               (rf/dispatch [::events/next-turn])))}
     (for [r (range rows)
           c (range columns)]
       ^{:key (str r "-" c)} [cell {:column c
                                    :row r
                                    :size cell-size}])]))

(defn footer []
  [:footer
   [:button {:type "button" :on-click #(rf/dispatch [::events/start-again])} "Click to start again"]])

(defn app []
  [:<>
   [header]
   [board {:columns 3 :rows 3}]
   [footer]])
