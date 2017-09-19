(ns canvas-demos.views
  (:require [canvas-demos.canvas :as canvas]
            [canvas-demos.events :as events]
            [reagent.core :as reagent]))

(defn canvas-inner []
  (reagent/create-class
   {:component-did-mount (fn [_]
                           (canvas/fullscreen-canvas!))
    :reagent-render      (fn [_]
                           [:canvas#canvas events/canvas-event-handlers])}))

(defn canvas []
  [:div#canvas-container {:style {:height "100%"
                                  :width "100%"
                                  :overflow "hidden"}}
   [canvas-inner]])
