(ns canvas-demos.core
  (:require [canvas-demos.canvas :as canvas]
            [canvas-demos.drawing :as drawing]))


(defn dev-setup []
  (when goog.DEBUG
    (enable-console-print!)
    (println "dev mode")))

(defn ^:export mount-root []
  (canvas/fullscreen-canvas!)
  (drawing/draw! (canvas/context (canvas/canvas-elem)) drawing/drawing))

(defn ^:export init []
  (dev-setup)
  (mount-root))
