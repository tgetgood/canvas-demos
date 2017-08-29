(ns canvas-demos.shapes.base
  (:require [canvas-demos.canvas :as canvas]
            [canvas-demos.drawing :as drawing]))

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
    (canvas/line ctx style p q)))

(defrecord Circle [style c r]
  drawing/Drawable
  (draw [_ ctx]
    (canvas/circle ctx style c r)))

(defrecord Rectangle [style p w h]
  drawing/Drawable
  (draw [_ ctx]
    (canvas/rectangle ctx style p (mapv + p [w 0] [0 h]))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;; API
;;
;; Separating the constructors from the records gives us a nicer API, and frees
;; us up to substitute special Vector and Scalar types for performing linear
;; algebra operations down the road without changing any of the downstream code.
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
   (Line. style p q)))

(defn rectangle
  ([{:keys [style p w h]}]
   (rectangle style p w h))
  ([p w h]
   (rectangle {} p w h))
  ([style p w h]
   (Rectangle. style p w h)))

(defn circle
  ([{:keys [style c r]}]
   (circle style c r))
  ([c r]
   (circle {}  c r))
  ([style c r]
   (Circle. style c r)))
