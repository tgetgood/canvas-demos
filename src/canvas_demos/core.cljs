(ns canvas-demos.core
  (:require [canvas-demos.canvas-utils :as canvas]
            [canvas-demos.db :as db]
            [canvas-demos.drawing :as drawing]
            [canvas-demos.views :as views]
            [reagent.core :as reagent]
            [paren-soup.core :as ps]))

(defn dev-setup []
  (when goog.DEBUG
    (enable-console-print!)
    (println "dev mode")))

(defn redraw! []
  (drawing/draw! @db/canvas @db/window))

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
                   (redraw!)))))))))

(defn refresh-app!
  [f]
  ;; Stop any playing animations

  ;; Redraw on window change
  (remove-watch db/window :main)
  (add-watch db/window :main f)
  (f))

(defn ^:export mount-root []
  (watch-resize!)
  (db/disconnect-editor!)
  (db/connect-editor!)

  (drawing/stop-animation!)
  (remove-watch db/window :main)
  (add-watch db/window :main redraw!)

  (reagent/render [views/main]
                  (.getElementById js/document "app")))


(defn ^:export init []
  (dev-setup)
  (db/init-editor!)
  (mount-root))
