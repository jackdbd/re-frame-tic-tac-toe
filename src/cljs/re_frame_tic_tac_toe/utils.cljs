(ns re-frame-tic-tac-toe.utils
  (:require [cljs.spec.alpha :as s]))

(defn check-spec-and-throw
  "Throws an exception if `db` doesn't match the spec `a-spec`."
  [a-spec db]
  (when-not (s/valid? a-spec db)
    (throw (ex-info (str "spec check failed: " (s/explain-str a-spec db)) {}))))

(defn filter-by-val
  [pred m]
  (into {} (filter (fn [[_ v]]
                     (pred v)) m)))

;; TODO: avoid i->coords and coords->i. How to detect if a cell is on a diagonal, given its index?

(defn make-i->coords
  [side]
  (fn i->coords
    [i]
    [(quot i side) (mod i side)]))

(defn make-coords->i
  [side]
  (fn coords->i [[row col]]
    (-> (* side row) (+ col))))
