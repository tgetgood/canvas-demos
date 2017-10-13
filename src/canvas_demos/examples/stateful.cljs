(ns canvas-demos.examples.stateful
  (:require [canvas-demos.shapes.affine :refer [translate]]
            [canvas-demos.shapes.base :refer [dd] :include-macros true]))

(def circle
  (dd [.beginPath
       (.arc 0 0 50 0 (* 2 js/Math.PI))
       .stroke
       .closePath]))

(def red-circle
  (dd [(aset "strokeStyle" "red" )
       .beginPath
       (.arc 0 0 50 0 (* 2 js/Math.PI))
       .stroke
       .closePath]))

(def safe-red-circle
  (dd [.save
       (aset "strokeStyle" "red" )
       .beginPath
       (.arc 0 0 50 0 (* 2 js/Math.PI))
       .stroke
       .closePath
       .restore]))

(def dotted-circle
  (dd [(.setLineDash #js [10 10])
       (aset "strokeStyle" "green")
       (.arc 0 0 50 0 (* 2 js/Math.PI))
       .stroke]))

(def demo
  [
   #_(translate dotted-circle 200 300)
   #_(translate red-circle 300 100)
   (translate circle 100 100)
   ])
