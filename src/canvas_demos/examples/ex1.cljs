(ns canvas-demos.examples.ex1
  "Demo of declarative drawing and composition"
  (:require [canvas-demos.shapes.base :as base :refer [circle line rectangle]]))

(def house
  [(rectangle {:line-width 5
               :stroke-style "purple"
               :fill "red"}
               [0 0] 140 100)
    (line [0 100] [70 (+ 100 70)])

    (line [140 100] [70 (+ 100 70)])])

(def picture
  '(concat
    (map #(apply translate house %)
            [[100 100] [300 100] [700 400] [1000 1000]])

    [(circle {:c [500 500]
              :r 321})

     (circle {:c [0 0]
              :r 225
              :style {:fill "purple"
                      :line-width 15}})

     (rectangle {:p [1000 1000]
                 :w 230
                 :h 45})

     (rectangle {:fill "pink"} [742 614] 10 10)

     (line {:style {:stroke-style "blue"
                    :line-width 5}
            :p [1000 1000]
            :q [1230 1045]})

     (line {:style {:stroke-style "red"
                    :line-width 5}
            :p [1000 1000]
            :q [1230 1000]})

     (line {:p [0 0]
            :q [1000 1000]})]))
