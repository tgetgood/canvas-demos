(ns canvas-demos.examples.ex3
  "Demo of shape manipulation via affine transformations"
  (:require [canvas-demos.drawing :as drawing]
            [canvas-demos.shapes.affine :as affine]
            [canvas-demos.shapes.base :refer [line rectangle]]))

(def boat
  "Ugly boat"
  [(rectangle {:p [400 400] :w 200 :h 40 :style {:fill "grey"}})
   (line {:p [400 400] :q [500 380]})
   ;; FIXME: How will we fill arbitrary regions?
   (line {:p [600 400] :q [500 380] :style {:fill "grey"}})
   (rectangle {:p [490 440] :w 20 :h 100 :style {:fill "grey"}})
   (line {:p [490 540] :q [420 440]})
   (line {:p [510 540] :q [580 440]})])

(def picture
  [(-> boat
       (affine/rotate [500 440] 45))
   (-> boat
       #_(affine/translate 400 400))])


(defn draw! []
  (drawing/draw! picture))
