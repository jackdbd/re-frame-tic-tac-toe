(ns re-frame-tic-tac-toe.logic)

(defn board-coordinates
  "[x y] coordinates of a board of size `size`."
  [size]
  (for [x (range size)
        y (range size)]
    [x y]))

(defn- transpose
  "Transpose a matrix"
  [rows]
  (apply mapv vector rows))

(defn- winning-diags [size]
  (let [coords (board-coordinates size)]
    [(filter #(= (first %) (second %)) coords)
     (filter #(= (dec size) (reduce + %)) coords)]))

(defn- winning-rows [size]
  (partition size (board-coordinates size)))

(defn- winning-lines [size]
  (transpose (winning-rows size)))

(defn winning-cell-sets [size]
  (concat (winning-rows size)
          (winning-lines size)
          (winning-diags size)))
