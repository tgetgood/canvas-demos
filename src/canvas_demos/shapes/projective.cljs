(ns canvas-demos.shapes.projective
  "Same basic building blocks as base, but with an extra layer underneath that
  allows zooming and panning on the canvas."
  (:refer-clojure :exclude [val vector])
  (:require [canvas-demos.canvas :as canvas]
            [canvas-demos.drawing :as drawing]
            [clojure.walk :as walk]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;; Projective Bases
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
    (Vector. (* z (+ ox x)) (* z (+ oy y))))

  Valuable
  (val [_] [x y]))

(defrecord Scalar [s]
  Projectable
  (project [_ {z :zoom}]
    (Scalar. (* z s)))

  Valuable
  (val [_] s))

(defn vector
  ([[x y]] (vector x y))
  ([x y] (Vector. x y)))

(defn scalar [s]
  (Scalar. s))

(defn project-all [picture window]
  (walk/prewalk
   (fn [o]
     (if (satisfies? Projectable o)
       (project o window)
       o))
   picture))

(defn projected-draw! [content]
  (let [[_ h]        (canvas/canvas-container-dimensions)
        ctx          (canvas/context (canvas/canvas-elem))
        window       {:zoom .5 :offset [747 619]}
        proj-content (project-all content window)]
    (canvas/clear ctx)
    ;; Use a fixed Affine tx to normalise coordinates.
    ;; REVIEW: Does resetting this on each frame hurt performance?
    (canvas/atx ctx 1 0 0 -1 0 h)
    (drawing/draw proj-content ctx)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;; Records
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(deftype
    ^{:doc
      "Composite type to hold an entire drawing. Acts as a collection containing
      shapes. Drawing order is undefined. Drawings can recursively contain
      drawings and will do the right thing"}
    Drawing
    [children]
  drawing/Drawable
  (draw [_ ctx]
    (doseq [shape children]
      (drawing/draw shape ctx)))

  IEmptyableCollection
  (-empty [_]
    (Drawing. []))

  ICollection
  (-conj [_ o]
    (Drawing. (conj children o)))

  ISeqable
  (-seq [_]
    (seq children))

  ;; FIXME: Terrible repl printing. Better than nothing though.
  IPrintWithWriter
  (-pr-writer [this writer opts]
    (-write writer "#canvas-demo.shapes.base.Drawing")
    (-write writer "[")
    (doseq [c children]
      (-write writer "\n")
      (-flush writer)
      (-pr-writer c writer opts))
    (-write writer "]")))

(defrecord Line [style p q]
  drawing/Drawable
  (draw [_ ctx]
    (canvas/line ctx style (val p) (val q))))

(defrecord Circle [style c r]
  drawing/Drawable
  (draw [_ ctx]
    (canvas/circle ctx style (val c) (val r))))

(defrecord Rectangle [style p w h]
  drawing/Drawable
  (draw [_ ctx]
    (let [p (val p)
          q (mapv + p [(val w) 0] [0 (val h)])]
      (canvas/rectangle ctx style p q))))

(defrecord Pixel [style p])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;; API
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn drawing [& shapes]
  ;; Use a vector so that later shapes are drawn on top of earlier. Makes
  ;; occlusion more obvious.
  (Drawing. (into [] shapes)))

(defn line
  ([{:keys [style p q]}]
   (line style p q))
  ([p q]
   (line {} p q))
  ([style p q]
   (Line. style (vector p) (vector q))))

(defn rectangle
  ([{:keys [style p w h]}]
   (rectangle style p w h))
  ([p w h]
   (rectangle {} p w h))
  ([style p w h]
   (Rectangle. style (vector p) (scalar w) (scalar h))))

(defn circle
  ([{:keys [style c r]}]
   (circle style c r))
  ([c r]
   (circle {}  c r))
  ([style c r]
   (Circle. style (vector c) (scalar r))))
