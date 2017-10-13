(ns canvas-demos.shapes.base
  (:require [canvas-demos.canvas :as canvas :include-macros true]
            [canvas-demos.shapes.affine :as affine]
            [canvas-demos.shapes.protocols :refer [draw Drawable]]))

;;;;; Text

(defrecord TextLine [style text position]
  Drawable
  (draw [_ ctx]
       (canvas/text ctx style text position)))

;;;;; Shapes

(defrecord Line [style from to]
  Drawable
  (draw [_ ctx]
    (canvas/line ctx style from to)))

(defrecord Circle [style centre radius]
  Drawable
  (draw [_ ctx]
    (canvas/circle ctx style centre radius)))

(defrecord Rectangle [style bottom-left width height]
  Drawable
  (draw [_ ctx]
    (let [bottom-left bottom-left
          top-right (mapv + bottom-left [width 0] [0 height])]
      (canvas/rectangle ctx style bottom-left top-right))))

(defrecord Arc [style centre radius from to]
  Drawable
  (draw [_ ctx]
    (canvas/arc ctx style centre radius from to)))

(defrecord Shape [style content]
  Drawable
  (draw [_ ctx]
    (canvas/with-style (.-ctx ctx) style
      ;; HACK: This state juggling can't be the right way to do this.
      (let [state (canvas/get-state ctx)]
        (canvas/clear-state! ctx)
        (doseq [shape content]
          (draw shape ctx))
        (canvas/set-state! ctx state)))))

;; Treat seq types as implicit shapes.

(extend-protocol Drawable
  PersistentVector
  (draw [this ctx]
    (draw (Shape. {} this) ctx))

  LazySeq
  (draw [this ctx]
    (draw (Shape. {} this) ctx))

  List
  (draw [this ctx]
    (draw (Shape. {} this) ctx)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;; API
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn with-style
  "Wraps shapes in a container shape with the given style applied to it."
  [style & shapes]
  (Shape. style shapes))

(defn pixel
  ([colour x y]
   (pixel colour [x y]))
  ([colour p]
   (Rectangle. {:fill-style colour
                :line-width 0.05
                :stroke-style colour}
               p .99 .99)))

(defn line
  ([{:keys [style from to]}]
   (line style from to))
  ([from to]
   (line {} from to))
  ([style from to]
   (Line. style from to)))

(defn rectangle
  ([{:keys [style bottom-left width height]}]
   (rectangle style bottom-left width height))
  ([bottom-left width height]
   (rectangle {} bottom-left width height))
  ([style bottom-left width height]
   (Rectangle. style bottom-left width height)))

(defn circle
  ([{:keys [style centre radius]}]
   (circle style centre radius))
  ([centre radius]
   (circle {} centre radius))
  ([style centre radius]
   (Circle. style centre radius)))

(defn arc
  ([{:keys [style centre radius from to]}]
   (arc style centre radius from to))
  ([centre radius from to]
   (arc {} centre radius from to))
  ([style centre radius from to]
   (Arc. style centre radius from to)))

;;;;; Text

(defn textline
  ([text]
   (textline {} text [0 0]))
  ([text position]
   (textline {} text position))
  ([style text [x y :as position]]
   (affine/reflect
    (TextLine. style text position)
    [x y] [1 0])))
