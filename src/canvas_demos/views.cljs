(ns canvas-demos.views
  (:require [canvas-demos.canvas :as canvas]
            [canvas-demos.events :as events]
            [reagent.core :as reagent]
            [canvas-demos.db :as db]))

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

(defn editor [text]
  [:textarea {:style {:height "100%"
                      :width "100%"
                      :resize "none"}
              :defaultValue text
              :on-change js/console.log}])

(defn main []
  [:div {:style {:overflow "hidden"
                 :height "100%"}}
   [:div {:style {:float "left"
                  :width "30%"
                  :height "100%"}}
    [editor "adasf"]]
   [:div {:style {:float "right"
                  :height "100%"
                  :width "70%"}}
    [canvas ]]])
