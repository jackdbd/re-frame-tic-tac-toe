(ns re-frame-tic-tac-toe.worker
  (:require [cljs.spec.alpha :as s]
            [re-frame-tic-tac-toe.message :as msg]))

(defn message-handler [^js e]
  (comment
    (if-not (s/valid? ::msg/event e)
      (do (println (str "spec check failed: " (s/explain-str ::msg/event e)))
          (js/self.postMessage (msg/message "error" (str "spec check failed: " (s/explain-str ::msg/event e)))))))
  
  (let [action (.. e -data -action)]
    (case action
      "notify" (js/postMessage (msg/message "notify-main" "Hi from worker"))
      :else (js/postMessage (msg/message "notify-main" (str "worker does not handle action " action))))))

(defn init []
  (js/self.addEventListener "message" message-handler))
