(ns canvas-demos.core
  (:require [canvas-demos.canvas :as canvas]
            [canvas-demos.drawing :as drawing]
            [canvas-demos.events :as events]
            [canvas-demos.examples.ex1 :as ex1]
            [canvas-demos.examples.grid :as grid]))

(defn dev-setup []
  (when goog.DEBUG
    (enable-console-print!)
    (println "dev mode")))

(def main
  "The thing you're currently working on"
  grid/draw!)

(defn ^:export mount-root []
  ;; Resize canvas
  (canvas/fullscreen-canvas!)

  ;; Reload event handlers
  (let [c (canvas/canvas-elem)]
    (events/remove-handlers! c)
    (events/register-handlers! c))

  ;; Kill any running animations
  (drawing/stop-animation!)

  ;; Redraw on window change
  (remove-watch events/window :main)
  (add-watch events/window :main main)
  (main))

(defn ^:export init []
  (dev-setup)
  (mount-root))
