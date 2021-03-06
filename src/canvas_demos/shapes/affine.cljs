(ns canvas-demos.shapes.affine
  "Declarative DSL for applying affine transforms to shapes. Isolates
  transformation to the shape to which it is applied. Can be nested.
  N.B.: Affine transformations don't commute, so the order in which you apply
  the basic transformers is important."
  (:refer-clojure :exclude [val])
  (:require [canvas-demos.canvas :as canvas]
            [canvas-demos.shapes.protocols :refer [draw Drawable]])
  (:require-macros [canvas-demos.shapes.affine :refer [with-origin]]))

(defn det [a b c d]
  (- (* a d) (* b c)))

(defn invert-atx
  "Returns matrix corresponding to the inverse affine transform."
  [[a b c d x y]]
  (let [abs (det a b c d)
        [a' b' c' d'] (map #(/ % abs) [d (- b) (- c) a])
        x' (- (+ (* a' x) (* c' y)))
        y' (- (+ (* b' x) (* d' y)))]
    [a' b' c' d' x' y']))

(defrecord AffineWrapper [base-shape atx]
  ;; Draw this shape with the given affine transform. Reset the global state
  ;; after so as to not effect other shapes.
  ;; N.B.: requires serial rendering.
  Drawable
  (draw [_ ctx]
    (canvas/apply-affine-tx ctx atx)
    (draw base-shape ctx)
    (canvas/apply-affine-tx ctx (invert-atx atx))))

(defn wrap-affine [base-shape atx]
  (AffineWrapper. base-shape atx))

(defn deg->rad [d]
  (* js/Math.PI (/ d 180)))

(defn translate
  "Returns a copy of shape translated by [x y],"
  [shape x y]
  (wrap-affine shape [1 0 0 1 x y]))

(defn rotate
  "Returns a copy of shape rotated by angle around the given centre of
  rotation."
  ([shape angle] (rotate shape [0 0] angle))
  ([shape centre angle]
   (let [r (deg->rad angle)
         c (Math.cos r)
         s (Math.sin r)]
     (with-origin shape centre
       (wrap-affine [c s (- s) c 0 0])))))

(defn scale
  "Returns a copy of shape scaled horizontally by a and verticaly by b. Centre
  is the origin (fixed point) of the transform."
  ([shape a]
   (scale shape [0 0] a a))
  ([shape a b] (scale shape [0 0] a b))
  ([shape centre a b]
   (with-origin shape centre
     (wrap-affine [a 0 0 b 0 0]))))

(defn reflect
  "Returns a copy of shaped reflected around the line with direction dir through
  centre."
  ([shape dir] (reflect shape [0 0] dir))
  ([shape centre [dx dy]]
   (if (= 0 dx)
     (with-origin shape centre
       (wrap-affine [-1 0 0 1 0 0]))
     (let [m    (/ dy dx)
           m2   (* m m)
           dem  (inc m2)
           diag (/ (- 1 m2) dem)
           off  (/ (* 2 m) dem)]
       (with-origin shape centre
         (wrap-affine [diag off off (- diag) 0 0]))))))
