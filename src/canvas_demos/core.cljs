(ns canvas-demos.core
  (:require [canvas-demos.canvas :as canvas]
            [canvas-demos.drawing :as drawing]
            [canvas-demos.events :as events]
            [canvas-demos.examples.ex3 :as ex3]))

(defn dev-setup []
  (when goog.DEBUG
    (enable-console-print!)
    (println "dev mode")))

(def main
  "The thing you're currently working on"
  ex3/draw!)

(defn ^:export mount-root []
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
  (add-watch events/window :main main)
  (main))

(defn ^:export init []
  (dev-setup)
  (mount-root))
