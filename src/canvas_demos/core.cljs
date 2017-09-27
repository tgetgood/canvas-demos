(ns canvas-demos.core
  (:require [canvas-demos.canvas-utils :as canvas]
            [canvas-demos.db :as db]
            [canvas-demos.drawing :as drawing]
            [canvas-demos.views :as views]
            [reagent.core :as reagent]))

(defn dev-setup []
  (when goog.DEBUG
    (enable-console-print!)
    (println "dev mode")))

(defonce  main
  ;; "The thing you're currently working on"
  ;; defonce doesn't take a docstring or metadata...
  (atom nil))

(defn watch-resize! []
  (let [running (atom false)]
    (set! (.-onresize js/window)
          (fn []
            (when (compare-and-set! running false true)
              (.requestAnimationFrame
               js/window
               (fn []
                 (when (compare-and-set! running true false)
                   (canvas/fullscreen-canvas!)))))))))

(defn refresh-app!
  [f]
  ;; Stop any playing animations
  (drawing/stop-animation!)

  ;; Redraw on window change
  (remove-watch db/window :main)
  (add-watch db/window :main f)
  (f))

(defn ^:export mount-root []
  (when @main
    (refresh-app! @main))
  (reagent/render [views/main]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (dev-setup)
  (watch-resize!)
  (mount-root))
