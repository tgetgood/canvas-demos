(ns canvas-demos.examples.grid
  (:require [canvas-demos.drawing :as drawing]
            [canvas-demos.events :as events]
            [canvas-demos.shapes.base :as base]))

(defrecord GridFrame [window shapes]
  drawing/Drawable
  (draw [_ ctx]

    ))

(defrecord Grid [shapes]
  drawing/Projectable
  (project [_ window]
    (GridFrame. window shapes)))

;;;;; Complex mult

(defn m
  "Returns project (a+bi)(c+di) as a pair"
  [a b c d]
  [(- (* a c) (* b d)) (+ (* a d) (* b c))])

(defn c2 [a b]
  [(- (* a a) (* b b)) (* 2 a b)])

(defn a [a b [c d]]
  [(+ a c) (+ b d)])

(defn n2 [a b]
  (+ (* a a) (* b b)))

;;;;; Mandlebrot

(defn mandlebrot-colour [n]
  "red")

(defn iteration-count [[x y]]
  (let [max-norm 100000000
        max-iter 1000]
    (loop [c 0
           za x
           zb y]
      (if (or (< max-iter c) (< max-norm (n2 za zb)))
        c
        (let [[za' zb'] (a x y (c2 za zb))]
          (recur (inc c) za' zb'))))))


(defrecord Mandlebrot [window]
  drawing/Drawable
  (draw [_ ctx]
    (let [{:keys [zoom height width] [x0 y0] :offset} window
          pixels (mapcat (fn [i]
                           (map (fn [j]
                                  [(+ x0 (/ i zoom)) (+ y0 (/ j zoom))])
                             (range height)))
                         (range width))
          colours (map iteration-count pixels)
          arr (js/Uint8ClampedArray. (* 4 height width))]
      (loop [[c & cs] colours
             i 0]
        (when c
          (aset arr (+ 3 (* i 4)) (mod c 255))
          (recur cs (inc i))))
      (let [img (js/ImageData. arr width height)]
        (.putImageData (.-ctx ctx) img 0 0)))))


(defn draw! []
  #_(drawing/draw! (Mandlebrot. @events/window)))
