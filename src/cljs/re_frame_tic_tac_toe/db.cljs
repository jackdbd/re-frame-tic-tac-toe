(ns re-frame-tic-tac-toe.db
  (:require
   [cljs.spec.alpha :as s]
   [re-frame-tic-tac-toe.logic :refer [winning-cell-sets]]))

(s/def ::size pos?)

(s/def ::player
  #{:x :o})

(s/def ::turn pos?)

(s/def ::board (s/keys :req-un [::size]))

(s/def ::db (s/keys :req-un [::board ::player ::turn]))

(def default-db
  "Default application state."
  {:board {:size 3}
   :player :x
   :winning-cell-sets (winning-cell-sets 3)
   :turn 1})
