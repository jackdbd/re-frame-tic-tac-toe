(ns re-frame-tic-tac-toe.logic
  (:require
   [clojure.set :refer [subset?]]
   [re-frame-tic-tac-toe.utils :refer [make-coords->i make-i->coords]]))

(defn- transpose
  "Transpose a matrix"
  [rows]
  (apply mapv vector rows))

(defn- on-diag-top-left-to-bottom-right?
  [[row col]]
  (= row col))

(defn- make-on-diag-bottom-left-to-top-right?
  [side]
  (fn on-diag-bottom-left-to-top-right? [[row col]]
    (= (dec side) (+ row col))))

;; TODO: avoid i->coords and coords->i. How to detect if a cell is on a diagonal, given its index?

;; TODO spec on function + instrumentation
(defn winning-sets
  "All winning combinations."
  [n]
  (let [side (.sqrt js/Math n)
        rows (partition side (range n))
        columns (transpose rows)
        i->coords (make-i->coords side)
        coords->i (make-coords->i side)
        on-diag-bottom-left-to-top-right? (make-on-diag-bottom-left-to-top-right? side)
        coords-on-diag-1 (->> (range n) (map i->coords) (filter on-diag-top-left-to-bottom-right?))
        coords-on-diag-2 (->> (range n) (map i->coords) (filter on-diag-bottom-left-to-top-right?))
        diag-1-set (->> coords-on-diag-1 (map coords->i) set)
        diag-2-set (->> coords-on-diag-2 (map coords->i) set)]
    (concat (map set rows)
            (map set columns)
            [diag-1-set diag-2-set])))

(defn make-winning-subset?
  "Build a predicate to check whether any of the winning sets is a subset of
  the marks placed by the player."
  [player-marks-set]
  (fn winning-subset? [winning-set]
    (subset? winning-set player-marks-set)))
