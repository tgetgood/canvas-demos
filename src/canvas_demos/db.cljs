(ns canvas-demos.db
  (:require [canvas-demos.canvas-utils :as canvas]
            [canvas-demos.examples.ex1 :as ex1]
            [canvas-demos.examples.ex3 :as ex3]
            [canvas-demos.examples.presentation :as presentation]
            [canvas-demos.examples.stateful :as stateful]))

;;;;; State

(defonce current-drawing (atom #'ex1/house))

(defonce ^:dynamic *redraw* true)

(defonce window (atom {:zoom 1 :offset [0 0] :width 0 :height 0}))

;;;;; Window mutations

(defn update-window-dimensions! []
  (let [[w h] (canvas/canvas-container-dimensions)]
    (swap! window assoc :width w :height h)))

(defn reset-zoom! []
  (swap! window assoc :zoom 1 :offset [0 0]))

;;;; Canvas mutations

(def var-table
  {:ex1          #'ex1/picture
   :house        #'ex1/house
   :blinky       #'ex1/blinky
   :election     #'ex3/election
   :state        #'stateful/demo
   :rings        #'presentation/rings
   :presentation #'presentation/go})

(declare slides)

(defn switch! [sym]
  (reset! slides nil)
  (reset! current-drawing (get var-table sym))
  (swap! window assoc :zoom 1 :offset [0 0]))

;;;;; Logic to turn this into presentation software

(defonce cursor (atom 0))

(defonce slides (atom nil))

(defn- set-slide-window []
  (when-let [{:keys [zoom offset]} (get @@slides @cursor)]
    (swap! window assoc :zoom zoom :offset (mapv (partial * zoom) offset))))

(defn start-pres [[pres shape]]
  (reset! slides pres)
  (reset! cursor 0)

  (reset! current-drawing shape)
  (set-slide-window))

(defn next-slide []
  (swap! cursor #(min (inc %) (count @@slides)))
  (set-slide-window))

(defn prev-slide []
  (swap! cursor #(max (dec %) 0))
  (set-slide-window))

(defn pres! []
  (start-pres canvas-demos.examples.presentation/pres))
