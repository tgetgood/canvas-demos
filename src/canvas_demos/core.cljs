(ns canvas-demos.core
  (:require [canvas-demos.canvas-utils :as canvas]
            [canvas-demos.db :as db]
            [canvas-demos.drawing :as drawing]
            [canvas-demos.events :as events]))

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
                   (let [[w h :as dim] (canvas/canvas-container-dimensions)]
                     (swap! db/window assoc :width w :height h))
                   (canvas/fullscreen-canvas!)
                   (drawing/redraw!)))))))))

(defn update-window! []
  (let [[w h] (canvas/canvas-container-dimensions)]
    (swap! db/window assoc :width w :height h)))

(defn ^:export mount-root []
  (canvas/fullscreen-canvas!)
  (watch-resize!)
  (update-window!)

  (drawing/stop-animation!)
  (remove-watch db/window :main)
  (add-watch db/window :main drawing/redraw!)

  (let [elem (.getElementById js/document "app")]
    (events/remove-handlers! elem)
    (events/register-handlers! elem))

  (drawing/redraw!))


(defn ^:export init []
  (dev-setup)
  (mount-root))
