(ns canvas-demos.examples.ex1
  "Demo of declarative drawing and composition"
  (:require [canvas-demos.shapes.affine :refer [translate]]
            [canvas-demos.shapes.base :as base :refer [circle line rectangle tt]]))

(def house
  (tt))

(def picture
  (concat
    [(circle {:style {:fill {:gradient {:from [200 800]
                                        :to [800 200]
                                        :stops {0 :hotpink
                                                1 :aquamarine}}}}
              :centre [500 500]
              :radius 321})

     (circle {:centre [0 0]
              :radius 225
              :style {:fill "purple"
                      :line-width 15}})

     (rectangle {:bottom-left [1000 1000]
                 :width 230
                 :height 45})

     (rectangle {:fill "pink"} [200 800] 10 10)
     (rectangle {:fill "pink"} [800 200] 10 10)

     (line {:style {:stroke "blue"
                    :line-width 5}
            :from [1000 1000]
            :to [1230 1045]})

    (map #(apply translate house %)
            [[100 100] [300 100] [700 400] [1000 1000]])

     (line {:style {:stroke-style "red"
                    :line-width 5}
            :from [1000 1000]
            :to [1230 1000]})

     (line {:style {:line-dash [10 10]}
            :from [0 0]
            :to [1000 1000]})]))
