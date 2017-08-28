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

(defn rc []
  (str "rgb("
       (rand-int 255) "," (rand-int 255) "," (rand-int 255)
       ")"))

(defn cfn [x y r]
  (lazy-seq
   (cons
    [{:type :circle
      :c [x y]
      :r r
      :style {:stroke-style (rc)
              :line-width 10}}]
    (cfn (+ x (- 15 (rand-int 30)))
         (+ y (- 15 (rand-int 30)))
         (max 0 (+ r (- 5 (rand-int 10))))))))

(defn c-seq []
  (cfn (rand-int 1000) (rand-int 1000) (rand-int 100)))

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


(defn go-circles! []
  (.log js/console "Run (stop!) to kill animation.")
  (animate! (apply map concat (take 200 (repeatedly c-seq)))))
