(ns canvas-demos.examples.ex2
  "Basic Animation Demo"
  (:require [canvas-demos.shapes.base :as base :refer [circle]]))

(defn rc []
  (str "rgb("
       (rand-int 255) "," (rand-int 255) "," (rand-int 255)
       ")"))

(defn cfn [x y r]
  (lazy-seq
   (cons
    (circle {:c [x y]
             :r r
             :style {:stroke-style (rc)
                     :line-width 10}})
    (cfn (min (max 0 (+ x (- 15 (rand-int 30)))) 1300)
         (min (max 0 (+ y (- 15 (rand-int 30)))) 1000)
         (min (max 0 (+ r (- 5 (rand-int 10)))) 100)))))

(defn c-seq []
  (cfn (rand-int 1000) (rand-int 1000) (rand-int 100)))

(def picture
  '(take 100 (apply interleave (take 100 (repeatedly c-seq)))))
