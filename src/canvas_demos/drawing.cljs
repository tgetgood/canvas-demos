(ns canvas-demos.drawing
  (:require [canvas-demos.drawing.impl :as impl]))

(defn house [[x y :as p]]
  [{:type :rectangle
    :style {:line-width 5
            :stroke-style "purple"
            :fill "red"}
    :p p
    :w 140
    :h 100}

   {:type :line
    :p [x (+ 100 y)]
    :q [(+ x 70) (+ y 100 70)]}

   {:type :line
    :p [(+ x 140) (+ y 100)]
    :q [(+ x 70) (+ y 100 70)]}])

(def drawing
  (mapcat house [[100 100] [300 100] [700 400]])
  #_[

       {:type :circle
        :c [500 500]
        :r 321}

       {:type :circle
        :c [0 0]
        :r 225
        :style {:fill "purple"
                :line-width 15}}

       {:type :rectangle
        :p [1000 1000]
        :w 230
        :h 45}

       {:type :line
        :style {:stroke-style "red"
                :line-width 5}
        :p [1000 1000]
        :q [1230 1045]}

       {:type :line
        :style {:stroke-style "red"
                :line-width 5}
        :p [1000 1000]
        :q [1230 1000]}

       {:type :line
        :p [0 0]
        :q [1000 1000]}])

;;;;; Outside API

(def draw! impl/draw!)
