(ns canvas-demos.drawing
  (:require [canvas-demos.canvas :as canvas]
            [canvas-demos.drawing.impl :as impl]))

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


(defn cfn [x]
   [{:type :circle
     :c [x 400]
     :r 300
     :style {:stroke-style (str "#" x)}}])

(def c-seq (map cfn (range)))

(def drawing
  (concat
   (mapcat house [[100 100] [300 100] [700 400]])

   [{:type :circle
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
     :q [1000 1000]}]))

;;;;; Outside API

(def draw! impl/draw!)
(def animate! impl/animate!)
(def stop! impl/kill-animation!)
