(ns canvas-demos.core
  (:require [canvas-demos.canvas-utils :as canvas]
            [canvas-demos.db :as db]
            [canvas-demos.drawing :as drawing]))

(defn dev-setup []
  (when goog.DEBUG
    (enable-console-print!)
    (println "dev mode")))

(defn watch-resize! []
  (let [running (atom false)]
    (set! (.-onresize js/window)
          (fn []
            (when (compare-and-set! running false true)
              (.requestAnimationFrame
               js/window
               (fn []
                 (when (compare-and-set! running true false)
                   (canvas/fullscreen-canvas!)
                   (drawing/redraw!)))))))))

(defn ^:export mount-root []
  (watch-resize!)

  (drawing/stop-animation!)
  (remove-watch db/current-drawing :main)
  (add-watch db/current-drawing :main drawing/redraw!)

  (canvas/fullscreen-canvas!)
  (drawing/redraw!))

(defn ^:export init []
  (dev-setup)
  (mount-root))
