(ns re-frame-tic-tac-toe.db
  (:require
   [cljs.spec.alpha :as s]))

(s/def ::turn pos?)
(s/def ::status #{:playing :game-over})
(s/def ::player #{:x :o})
(s/def ::mark (s/nilable ::player))
(s/def ::cell-index keyword) ;; TODO: make it better
(s/def ::board (s/map-of ::cell-index ::mark))

(s/def ::db (s/keys :req-un [::board ::player ::status ::turn]))

(defn new-board
  [n]
  (zipmap (->> (range n) (map #(keyword (str %))))
          (->> (repeat nil) (take n))))

(defn new-db
  [n]
  {:board (new-board n)
   :player :x
   :status :playing
   :turn 1})

(def default-db
  "Default application state."
  (new-db 9))
