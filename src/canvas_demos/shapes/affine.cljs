(ns canvas-demos.shapes.affine
  "Declarative DSL for applying affine transforms to shapes. Isolates
  transformation to the shape to which it is applied. Can be nested.
  N.B.: Affine transformations don't commute, so the order in which you apply
  the basic transformers is important."
  (:require [canvas-demos.canvas :as canvas]
            [canvas-demos.drawing :as drawing]))

(defn det [a b c d]
  (- (* a d) (* b c)))

(defn invert-atx
  "Returns matrix corresponding to the inverse affine transform."
  [[a b c d x y]]
  (let [abs (det a b c d)
        [a' b' c' d'] (map (partial * abs) [d (- b) (- c) a])
        x' (- (+ (* a' x) (* c' y)))
        y' (- (+ (* b' x) (* d' y)))]
    [a' b' c' d' x' y']))

(defrecord AffineWrapper [base-shape atx]
  ;; Draw this shape with the given affine transform. Reset the global state
  ;; after so as to not effect other shapes.
  ;; N.B.: requires serial rendering.
  drawing/Drawable
  (draw [_ ctx]
    (canvas/apply-affine-tx ctx atx)
    (drawing/draw base-shape ctx)
    (canvas/apply-affine-tx ctx (invert-atx atx))))

(defn deg->rad [d]
  (* js/Math.PI (/ d 180)))

(defn translate [shape x y]
  (AffineWrapper. shape [1 0 0 1 x y]))

(defn rotate
  "Returns a copy of shape rotated by angle around the given centre of
  rotation. units can be either :degrees (default) or :radians."
  [shape [x y :as centre] angle & [units]]
  (let [r (if (= units :radians) angle (deg->rad angle))
        c (Math.cos r)
        s (Math.sin r)]
    (-> shape
        (translate (- x) (- y))
        (AffineWrapper. [c s (- s) c 0 0])
        (translate x y))))
