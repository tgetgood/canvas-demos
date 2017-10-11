(ns canvas-demos.db
  (:require [canvas-demos.canvas-utils :as canvas]
            [canvas-demos.examples.ex1 :as ex1]))

;;;;; State

(defonce current-drawing (atom #'ex1/house))

(defonce window (atom {:zoom 1 :offset [0 0] :width 0 :height 0}))

;;;;; Window mutations

(defn update-window-dimensions! []
  (let [[w h] (canvas/canvas-container-dimensions)]
    (swap! window assoc :width w :height h)))

(defn reset-zoom! []
  (swap! window assoc :zoom 1 :offset [0 0]))

;;;; Canvas mutations

(def var-table
  {:ex1 #'ex1/picture
   :house #'ex1/house})

(defn switch! [sym]
  (reset! current-drawing (get var-table sym)))
