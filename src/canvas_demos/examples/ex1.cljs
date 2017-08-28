(ns canvas-demos.examples.ex1
  "Demo of declarative drawing and composition"
  (:require [canvas-demos.shapes.base :as base
             :refer [line circle rectangle]]))

(defn house [[x y :as p]]
  (base/drawing
   (rectangle {:line-width 5
               :stroke-style "purple"
               :fill "red"}
              p 140 100)

   (line [x (+ 100 y)] [(+ x 70) (+ y 100 70)])

   (line [(+ x 140) (+ y 100)] [(+ x 70) (+ y 100 70)])))

(def picture
  (into (base/drawing)
        (concat
         (mapcat house [[100 100] [300 100] [700 400] [1000 1000]])

         [(circle {:c [500 500]
                   :r 321})

          (circle {:c [0 0]
                   :r 225
                   :style {:fill "purple"
                           :line-width 15}})

          (rectangle {:p [1000 1000]
                      :w 230
                      :h 45})

          (line {:style {:stroke-style "blue"
                         :line-width 5}
                 :p [1000 1000]
                 :q [1230 1045]})

          (line {:style {:stroke-style "red"
                         :line-width 5}
                 :p [1000 1000]
                 :q [1230 1000]})

          (line {:p [0 0]
                 :q [1000 1000]})])))
