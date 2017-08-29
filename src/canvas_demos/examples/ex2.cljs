(ns canvas-demos.examples.ex2
  "Basic Animation Demo"
  (:require [canvas-demos.drawing :as drawing]
            [canvas-demos.shapes.base :as base :refer [circle]]))

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
    (cfn (+ x (- 15 (rand-int 30)))
         (+ y (- 15 (rand-int 30)))
         (max 0 (+ r (- 5 (rand-int 10))))))))

(defn c-seq []
  (cfn (rand-int 1000) (rand-int 1000) (rand-int 100)))

(defn combine [& shapes]
  (into (base/drawing) shapes))

(defn go-circles! []
  (.log js/console "Run (stop!) to kill animation.")
  (drawing/animate! (apply map combine (take 100 (repeatedly c-seq)))))