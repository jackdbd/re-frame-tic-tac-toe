(ns re-frame-tic-tac-toe.db
  (:require
   [cljs.spec.alpha :as s]))

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
   :turn 1})

;; TODO: store winning-collections-sets in Local Storage?