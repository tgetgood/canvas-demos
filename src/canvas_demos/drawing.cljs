(ns canvas-demos.drawing
  (:require [canvas-demos.drawing.impl :as impl]))

(def drawing
  [{:type :rectangle
    :style {:line-width 5
            :stroke-style "purple"
            :fill "pink"}
    :p [400 600]
    :w 30
    :h 100}

   {:type :circle
    :c [500 500]
    :r 321}

   {:type :line
    :style {:stroke-style "red"
            :line-width 5}
    :p [0 0]
    :q [1000 1000]}])

(def house
  [])

;;;;; Outside API

(def draw! impl/draw!)
