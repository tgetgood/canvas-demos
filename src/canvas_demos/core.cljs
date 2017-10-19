(ns canvas-demos.core
  (:require [canvas-demos.canvas-utils :as canvas]
            [canvas-demos.db :as db]
            [canvas-demos.drawing :as drawing]
            [canvas-demos.events :as events]
            [canvas-demos.examples.ex1 :as ex1]
            [canvas-demos.examples.ex3 :as ex3]
            [canvas-demos.examples.presentation :as presentation]
            [canvas-demos.examples.stateful :as stateful]))

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

;;;;; Playing around

(def var-table
  {:ex1          #'ex1/picture
   :house        #'ex1/house
   :blinky       #'ex1/blinky
   :election     #'ex3/election
   :state        #'stateful/demo
   :rings        #'presentation/rings
   :presentation #'presentation/go})

(defn switch! [sym]
  (drawing/stop-animation!)
  (reset! db/slides nil)
  (reset! db/current-drawing (get var-table sym))
  (swap! db/window assoc :zoom 1 :offset [0 0]))

(defn presentation! []
  (db/start-pres presentation/pres))
