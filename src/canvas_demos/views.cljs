(ns canvas-demos.views
  (:require [canvas-demos.canvas-utils :as canvas]
            [canvas-demos.events :as events]
            [reagent.core :as reagent]
            [canvas-demos.db :as db]))

(defn window-width []
  (.-innerWidth js/window))

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

(defn image-selector []
  (into [:div]
        (map (fn [name]
               [:button {:on-click (fn [_]
                                     #_(db/set-current-drawing! name))}
                name])
          (keys @db/drawings))))

(defn main []
  (fn []
    (let [button-width 80
          code-width 400
          canvas-width (- (window-width) button-width code-width)]
      [:div {:style {:overflow "hidden"
                     :height "100%"}}
       [:div {:style {:height "100%"
                      :float "left"
                      :width (+ code-width button-width)}}
        [:div {:style {:float "left"
                       :width code-width
                       :height "100%"}}
         [editor @db/editor-content]]
        [:div {:style {:float "right"
                       :width button-width
                       :height "100%"}}
         [image-selector]]]
       [:div {:style {:float "right"
                      :height "100%"
                      :width "70%"}}
        [canvas ]]])))
