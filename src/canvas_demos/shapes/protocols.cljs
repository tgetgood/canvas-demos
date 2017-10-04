(ns canvas-demos.shapes.protocols
  (:refer-clojure :exclude [val]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;; Drawing
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defprotocol Drawable
  (draw [this ctx]))

(extend-protocol Drawable
  default
  (draw [this _]
    (.error js/console (str "I don't know how to draw a " (type this))))

  nil
  (draw [_ _]
    (.error js/console "Can't draw a nil."))

  PersistentVector
  (draw [this ctx]
    (doseq [s this]
      (draw s ctx)))

  LazySeq
  (draw [this ctx]
    (doseq [s this]
      (draw s ctx)))

  List
  (draw [this ctx]
    (doseq [s this]
      (draw s ctx))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;; Projection
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defprotocol Valuable
  (val [this]))

(extend-protocol Valuable
  default
  (val [this] this))

(defprotocol Projectable
  (project [this window]))

(defrecord Vector [x y]
  Projectable
  (project [this {z :zoom [ox oy] :offset}]
    (Vector. (+ ox (* z x)) (+ oy (* z y))))

  Valuable
  (val [_] [x y]))

(defrecord Scalar [s]
  Projectable
  (project [_ {z :zoom}]
    (Scalar. (* z s)))

  Valuable
  (val [_] s))

(defrecord Constant [value]
  Projectable
  (project [this window] this)

  Valuable
  (val [_] value))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;; API
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn vec2
  "Returns a vector which responds correctly to linear transformation."
  ([[x y]] (vec2 x y))
  ([x y] (Vector. x y)))

(defn scalar
  "Returns a scalar object wrapping value s which acts appropriately under
  linear transformation."
  [s]
  (Scalar. s))

(defn constant
  "Dummy wrapper to protect contants from transformation."
  [v]
  (Constant. v))
