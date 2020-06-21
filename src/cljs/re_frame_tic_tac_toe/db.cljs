(ns re-frame-tic-tac-toe.db
  (:require [cljs.spec.alpha :as s]))

(s/def ::turn pos?)

(s/def ::player
  #{:player-a :player-b})

(s/def ::db (s/keys :req-un [::turn ::player]))

(def default-db
  "Default application state."
  {:player :player-a
   :turn 1})
