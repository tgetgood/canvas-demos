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
  (affine/with-origin
    [#_(-> boat
           #_(affine/rotate [500 440] 45))
     (affine/scale boat [400 400] 2 2)
     (affine/with-origin boat [400 400]
       (affine/scale 3 2)
       (affine/rotate 120)
       (affine/translate 100 -100))

     (affine/with-origin boat [400 400]
       (affine/reflect [1 1]))

     boat
     #_(-> boat
           (affine/scale [500 440] 7 2)
           (affine/rotate [500 440] 45)
           #_(affine/translate 400 400))]
    [600 100]
    (affine/reflect [0 1])
    (affine/rotate -60)
    (affine/translate -300 400)))


(defn draw! []
  (drawing/draw! picture))
