(ns canvas-demos.examples.ex1
  "Demo of declarative drawing and composition"
  (:require [canvas-demos.shapes.affine :refer [rotate scale translate]]
            [canvas-demos.shapes.base
             :as
             base
             :refer
             [->Raw circle line rectangle shape with-style]]))

(def t
  (->Raw))

(def messy-triangle
  (shape
   [(line {:stroke :cyan} [0 0] [400 0])
    (line {:line-width 1} [400 0] [200 200])
    (line  {:stroke :red
            :line-width 6}  [200 200] [0 0])]))

(def triangle
  (shape
   [(line [0 0] [400 0])
    (line [400 0] [200 200])
    (line [200 200] [0 0])]))

(def filled-messy
  [(with-style {:stroke "rgba(0,0,0,0)"
                :fill :magenta}
     triangle)
   messy-triangle])

(def house
  [
   (with-style {:line-width 10
                :fill :yellow}
     (translate triangle 0 300))
   (rectangle {:line-width 5
               :fill :blue}
              [0 0] 400 295)])

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



    (line {:style {:stroke-style "red"
                   :line-width 5}
           :from [1000 1000]
           :to [1230 1000]})

    (line {:style {:line-dash [10 10]}
           :from [0 0]
           :to [1000 1000]})

    (map #(apply translate (scale house 0.5 0.5) %)
      [[100 100] [500 100] [700 700] ])]))

(def leaf-outline
  [(line [0 0] [100 50])
   (line [100 50] [140 120])
   (line [140 120] [80 140])
   (line [80 140] [30 100])
   (line [30 100] [0 0])])

(def stem
  [(line {:line-width 10}
     [135 120] [190 170])])

(def texture
  [(line [0 0] [140 120])
   (line [95 50] [35 95])
   (line [81 135] [120 88])])

(def leaf
  [(with-style {:stroke :lightgreen}
     (with-style {:fill :green}
       leaf-outline)
     stem)
   (with-style {:line-dash [3]}
     texture)])
