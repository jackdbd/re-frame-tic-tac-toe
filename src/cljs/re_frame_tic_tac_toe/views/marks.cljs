(ns re-frame-tic-tac-toe.views.marks)

(defn x-mark
  "Symbol for player X."
  [{:keys [data-index padding size-px]}]
  [:<>
   [:line.x-mark {:x1 padding :y1 padding
                  :x2 (- size-px padding) :y2 (- size-px padding)
                  :data-index data-index}]
   [:line.x-mark {:x1 padding :y1 (- size-px padding)
                  :x2 (- size-px padding) :y2 padding
                  :data-index data-index}]])

(defn o-mark
  "Symbol for player O."
  [{:keys [data-index padding size-px]}]
  (let [r (- (/ size-px 2) padding)
        cx (+ r padding)
        cy cx]
    [:circle.o-mark {:cx cx :cy cy :r r
                     :data-index data-index}]))