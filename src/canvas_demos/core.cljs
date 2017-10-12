(ns canvas-demos.core
  (:require [canvas-demos.canvas-utils :as canvas]
            [canvas-demos.db :as db]
            [canvas-demos.drawing :as drawing]
            [canvas-demos.events :as events]))

(defn dev-setup []
  (when goog.DEBUG
    (enable-console-print!)
    (println "dev mode")))

(defn maybe-redraw []
  (when db/*redraw*
    (drawing/redraw!)))

(defn go []
  (let [[w h] (canvas/canvas-container-dimensions)]
    (swap! db/window assoc :width w :height h))

  (when db/*redraw*
    (canvas/fullscreen-canvas!)
    (drawing/redraw!)))

(defn watch-resize! []
  (let [running (atom false)]
    (set! (.-onresize js/window)
          (fn []
            (when (compare-and-set! running false true)
              (.requestAnimationFrame
               js/window
               (fn []
                 (when (compare-and-set! running true false)
                   (go)))))))))


(defn ^:export mount-root []
  (drawing/stop-animation!)
  (remove-watch db/current-drawing :main)
  (add-watch db/current-drawing :main maybe-redraw)

  (remove-watch db/window :main)
  (add-watch db/window :main maybe-redraw)

  (let [elem (.getElementById js/document "app")]
    (events/remove-handlers! elem)
    (events/register-handlers! elem))

  (watch-resize!)
  (go))

(defn ^:export init []
  (dev-setup)
  (mount-root))
