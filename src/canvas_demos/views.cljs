(ns canvas-demos.views
  (:require [canvas-demos.canvas-utils :as canvas]
            [paren-soup.core :as ps]
            [canvas-demos.db :as db]
            [canvas-demos.drawing :as drawing]
            [canvas-demos.events :as events]
            [reagent.core :as reagent]))

(defn canvas-inner []
  (reagent/create-class
   {:component-did-mount (fn [this]
                           (let [[w h :as dim] (canvas/canvas-container-dimensions) ]
                             (swap! db/window assoc :width w :height h))
                           (canvas/fullscreen-canvas!)
                           (let [drawing (:img (reagent/props this))]
                             (drawing/draw! drawing @db/window)))
    :component-did-update (fn [this]
                            (let [drawing (:img (reagent/props this))]
                             (drawing/draw! drawing @db/window)))
    :reagent-render      (fn [_]
                           [:canvas#canvas events/canvas-event-handlers])}))

(defn canvas [drawing]
  [:div#canvas-container {:style {:height   "100%"
                                  :width    "100%"
                                  :overflow "hidden"}}
   [canvas-inner {:img drawing}]])

(defn image-selector []
  (into [:div]
        (map (fn [name]
               [:button {:on-click (fn [_]
                                     (db/set-current-drawing! name))}
                name])
          (keys @db/drawings))))

(defn main []
  (fn []
    (let []
      [:div {:style {:overflow "hidden"
                     :height   "100%"}}
       [:div {:style {:position "absolute"
                      :z-index "10"
                      :float "left"
                      :width  "5%"
                      :height "100%"}}
        [image-selector]]
       [:div {:style {:position "absolute"
                      :height "100%"
                      :width  "100%"}}
        [canvas @db/canvas]]])))
