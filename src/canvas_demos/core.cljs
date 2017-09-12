(ns canvas-demos.core
  (:require [canvas-demos.canvas :as canvas]
            [canvas-demos.drawing :as drawing]
            [canvas-demos.events :as events]
            canvas-demos.examples
            canvas-demos.examples.ex1))

(defn dev-setup []
  (when goog.DEBUG
    (enable-console-print!)
    (println "dev mode")))

(defonce  main
  ;; "The thing you're currently working on"
  ;; defonce doesn't take a docstring or metadata...
  (atom canvas-demos.examples.ex1/draw!))

(defn refresh-app!
  [f]
  ;; Resize canvas
  (canvas/fullscreen-canvas!)

  ;; Stop any playing animations
  (drawing/stop-animation!)

  ;; Reload event handlers
  (let [c (canvas/canvas-elem)]
    (events/remove-handlers! c)
    (events/register-handlers! c))

  ;; Redraw on window change
  (remove-watch events/window :main)
  (add-watch events/window :main f)
  (f))

(defn ^:export mount-root []
  (when (fn? @main)
    (refresh-app! @main)))

(defn ^:export init []
  (dev-setup)
  (mount-root))
