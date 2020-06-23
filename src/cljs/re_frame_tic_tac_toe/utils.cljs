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
