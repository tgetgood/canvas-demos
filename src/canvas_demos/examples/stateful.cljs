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

(def screen-box
  (let [[w h] (canvas-utils/canvas-container-dimensions)]
    (rectangle [0 0] w h)))

(def code-box
  (rectangle {:style {:fill "#E1E1E1"
                      :stroke "rgba(0,0,0,0)"}
              :bottom-left [0 0]
              :width 1
              :height 1}))


(def nav-demo
  [[screen-box
    (-> code-box
        (scale 100 80)
        (translate 90 350))
    (textline {:font "18px serif"} "(circle)" [100 400])
    (textline {:font "18px serif"} "(red-circle)" [100 380])

    circle
    red-circle]])

(-> [screen-box
     red-circle
     circle]
    (scale 0.08)
    (translate 500 100))
(-> [screen-box
     safe-red-circle
     circle]
    (scale 0.08)
    (translate 500 100)
    (scale [500 100] 0.08)
    (translate 500 100))
