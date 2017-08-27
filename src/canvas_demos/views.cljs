(ns canvas-demos.views
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [canvas-demos.events :as events]
            [canvas-demos.events.dom :as dom-events]))

(defn canvas-inner []
  (reagent/create-class
   {:component-did-mount  (fn [this]
                            (let [elem (reagent/dom-node this)]
                              (set! (.-onresize js/window)
                                    #(events/resize-canvas-debounced elem))
                              (re-frame/dispatch [::events/resize-canvas elem])))
    :component-did-update (fn [this]
                            (let [elem (reagent/dom-node this)]
                              (re-frame/dispatch
                               [::events/redraw-canvas elem])))
    :reagent-render       (fn []
                            [:canvas
                             (assoc dom-events/canvas-event-handlers
                                    :id "the-canvas")])}))

(defn canvas-panel []
  [:div {:id "canvas-container"
         :on-drag-over #(.preventDefault %)
         :on-drag-enter #(.preventDefault %)
         :style {:width "100%"
                 :height "100%"
                 :overflow "hidden"}}
   ;; HACK: Custom types don't get picked up by reagent/props. They can be taken
   ;; out at a lower level, but I don't see that as any better.
   [canvas-inner]])

(defn main-panel []
  (fn []
    [:div {:style {:height "100%"}}
     [canvas-panel]]))
