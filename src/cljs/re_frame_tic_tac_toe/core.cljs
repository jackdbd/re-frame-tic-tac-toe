(ns re-frame-tic-tac-toe.core
  (:require
   [reagent.dom :as rdom]
   [re-frame.core :as rf]
   [re-frame-tic-tac-toe.events :as events]
   [re-frame-tic-tac-toe.views :as views]
   [re-frame-tic-tac-toe.config :as config]
  ;  [devtools.core :as devtools]
   ))


(defn dev-setup []
  ;; enable https://github.com/binaryage/cljs-devtools
  ; (devtools/install!)
  ;; This line allows us to use `(println "foo")` in place of (.log js/console "foo")
  (enable-console-print!))

(defn ^:dev/after-load mount-root
  "The `:dev/after-load `metadata causes this function to be called after
  shadow-cljs hot-reloads code. We force a UI update by clearing the Reframe
  subscription cache.
  https://github.com/day8/re-frame/blob/master/docs/FAQs/Why-Clear-Sub-Cache.md"
  []
  (rf/clear-subscription-cache!)
  (let [root-el (.getElementById js/document "app")]
    (rdom/unmount-component-at-node root-el)
    (rdom/render [views/app] root-el)))

(defn ^:export init
  "Initialize app-db first, then mount the root element in the DOM."
  []
  (rf/dispatch-sync [::events/initialize-db])
  (when config/debug?
    (dev-setup))
  (mount-root))
