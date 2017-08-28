(ns canvas-demos.core
  (:require [canvas-demos.canvas :as canvas]
            [canvas-demos.drawing :as drawing]
            [canvas-demos.examples.ex1 :as ex1]))


(defn dev-setup []
  (when goog.DEBUG
    (enable-console-print!)
    (println "dev mode")))

(defn ^:export mount-root []
  (canvas/fullscreen-canvas!)
  (drawing/draw! ex1/picture))

(defn ^:export init []
  (dev-setup)
  (mount-root))
