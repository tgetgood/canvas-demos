(ns canvas-demos.examples.ex3
  "Demo of shape manipulation via affine transformations"
  (:require [canvas-demos.shapes.base :refer [line rectangle]]))

(def boat
  "Ugly boat"
  '[(rectangle {:bottom-left [400 400]
                :width 200 :height 40 :style {:fill "grey"}})
   (line {:from [400 400] :to [500 380]})
   ;; FIXME: How will we fill arbitrary regions?
   (line {:from [600 400] :to [500 380] :style {:fill "grey"}})
   (rectangle {:bottom-left [490 440]
               :width 20 :height 100 :style {:fill "grey"}})
   (line {:from [490 540] :to [420 440]})
   (line {:from [510 540] :to [580 440]})])

(def picture
  '[#_(-> boat
          #_(rotate [500 440] 45))
    (scale boat [400 400] 2 2)
    (scale boat [400 400] 3 2)
    (rotate boat [400 400] 120)
    (translate 100 -100)

    (reflect boat [400 400] [1 1])

    boat
    #_(-> boat
          (scale [500 440] 7 2)
          (rotate [500 440] 45)
          #_(translate 400 400))]
  )
