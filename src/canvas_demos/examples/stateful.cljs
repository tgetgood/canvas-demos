(ns canvas-demos.examples.stateful
  (:require [canvas-demos.shapes.affine :refer [translate]]
            [canvas-demos.canvas-utils :as canvas-utils]
            [canvas-demos.shapes.base :refer [rectangle dd] :include-macros true]
            [canvas-demos.shapes.affine :refer [scale]]
            [canvas-demos.shapes.base :refer [textline]]))

(def circle
  (dd [.beginPath
       (.arc 0 0 50 0 (* 2 js/Math.PI))
       .closePath
       .stroke]))

(def red-circle
  (dd [(aset "strokeStyle" "red" )
       .beginPath
       (.arc 0 0 50 0 (* 2 js/Math.PI))
       .closePath
       .stroke]))

(def safe-red-circle
  (dd [.save
       (aset "strokeStyle" "red" )
       .beginPath
       (.arc 0 0 50 0 (* 2 js/Math.PI))
       .closePath
       .stroke
       .restore]))

(def dotted-circle
  (dd [(.setLineDash #js [10 10])
       (aset "strokeStyle" "green")
       (.arc 0 0 50 0 (* 2 js/Math.PI))
       .stroke]))

(def demo
  [
   safe-red-circle
   circle
   dotted-circle
   ])
