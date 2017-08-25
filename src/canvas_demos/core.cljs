(ns canvas-demos.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [canvas-demos.events :as events]
            [canvas-demos.subs]
            [canvas-demos.views :as views]
            [canvas-demos.config :as config]))


(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))

(defn ^:export mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (re-frame/dispatch-sync [::events/initialize-db])
  (dev-setup)
  (mount-root))
