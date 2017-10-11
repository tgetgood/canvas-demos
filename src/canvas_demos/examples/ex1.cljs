(ns canvas-demos.examples.ex1
  "Demo of declarative drawing and composition"
  (:require [canvas-demos.shapes.affine :refer [scale translate]]
            [canvas-demos.shapes.base
             :as
             base
             :refer
             [->Raw circle line pixel rectangle shape with-style]]))

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

(def grid
  (into #{}
        (mapcat (fn [x]
                  (map (fn [y]
                         [x y])
                    (range 14)))
                (range 14))))

(def eye
  [(map (partial pixel :lightgrey)
     [[1 3] [2 3]
      [0 2] [1 2] [2 2] [3 2]
      [2 1] [3 1]
      [2 0] [3 0]
      [1 -1] [2 -1]])
   (map (partial pixel :blue)
     [[0 0] [0 1] [1 0] [1 1]])])

(def mask
  [[1 0] [2 0] [3 0] [4 0]
   [2 1]

   [6 0] [6 1]
   [7 0] [7 1]

   [10 0] [11 0] [12 0]
   [11 1]

   [0 8] [0 9] [0 10] [0 11] [0 12] [0 13] [0 14]
   [1 11] [1 12] [1 13]
   [2 12] [2 13]
   [3 13]
   [4 13]

   [9 13] [10 13] [11 13] [12 13] [13 13]
   [11 12] [12 12] [13 12]
   [12 11] [13 11]
   [13 10]
   [13 9]
   [13 8]])

(def blinky
  [(map (partial pixel :red)
     (apply disj grid mask))
   (translate eye 1 7)
   (translate eye 7 7)])


(def leaf
  [(pixel :orange [-5 -5])

   (with-style {:stroke :lightgreen}
     (with-style {:fill :green}
       leaf-outline)
     stem)
   (with-style {:line-dash [3]}
     texture)])
