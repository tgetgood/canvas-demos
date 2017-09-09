(ns canvas-demos.examples.mandelbrot
  (:require [canvas-demos.drawing :as drawing]
            [canvas-demos.events :as events]
            [canvas-demos.shapes.base :as base]))

;;;;; Complex mult

(defn mult
  "Returns project (a+bi)(c+di) as a pair"
  [a b c d]
  [(- (* a c) (* b d)) (+ (* a d) (* b c))])

(defn c2 [a b]
  [(- (* a a) (* b b)) (* 2 a b)])

(defn add [[a b] [c d]]
  [(+ a c) (+ b d)])

(defn n2 [[a b]]
  (+ (* a a) (* b b)))

;;;;; Mandlebrot

(defn points-from-window
  [{:keys [zoom height width] [x y] :offset}]
  (mapcat (fn [i]
            (map (fn [j]
                   [[i j] [(/ (- i x) zoom) (/ (- j y) zoom)]])
              (range height)))
          (range width)))

(defn step-pixel [pix [x y] zoom c]
  (if (number? pix)
    pix
    (let [[[i j] [a b]] pix
          v (add (c2 a b) [(/ (- i x) zoom) (/ (- j y) zoom)])
          n (n2 v)]
      (if (< 4 n)
        c
        [[i j] v]))))

(defn step [ps z offset c]
  (map #(step-pixel % offset z c) ps))

(defn mandelbrot [{:keys [zoom offset] :as window}]
  (loop [ps (points-from-window window)
         i 0]
    (if (< 255 i)
      ps
      (recur (step ps zoom offset i) (inc i)))))

(defrecord Mandlebrot [window]
  drawing/Drawable
  (draw [_ ctx]
    (let [escape                 (mandelbrot window)
          {:keys [height width]} window
          arr                    (js/Uint8ClampedArray. (* 4 height width))
          img                    (js/ImageData. arr width height)]
      (loop [[c & cs] escape
             i        0]
        (when c
          (if (number? c)
            (aset (.-data img) (+ 1 (* i 4)) (mod c 255))
            (do
              (aset (.-data img) (* i 4) 255)
              (aset (.-data img) (+ 1 (* i 4)) 255)
              (aset (.-data img) (+ 2 (* i 4)) 255)))
          (recur cs (inc i))))
      (.log js/console img)
      (.putImageData (.-ctx ctx) img 0 0))))


(defn window-update [{:keys [width height] :as w}]
  (assoc w
         :zoom 350
         :offset [(quot width 2) (quot height 2)]))

(defn draw! []
  ;; Don't use this. Very expensive and doesn't do anything.
  ;; This guy is a tangent and should be cut sooner than later.
  (drawing/draw! (Mandlebrot. @events/window)))
