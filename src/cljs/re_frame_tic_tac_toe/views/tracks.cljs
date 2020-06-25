(ns re-frame-tic-tac-toe.views.tracks)

(defn tracks
  "Horizontal and vertical lines (i.e. the grid of Tic Tac Toe)."
  [side-px cell-px]
  [:g.tracks
   (for [x (range cell-px side-px cell-px)]
     ^{:key (str "track-hor-" x)} [:line.track {:x1 x :y1 0 :x2 x :y2 side-px}])
   (for [y (range cell-px side-px cell-px)]
     ^{:key (str "track-ver-" y)} [:line.track {:x1 0 :y1 y :x2 side-px :y2 y}])])
