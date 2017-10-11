(ns canvas-demos.shapes.base
  (:require [canvas-demos.canvas :as canvas :include-macros true]
            [canvas-demos.shapes.protocols :refer [draw Drawable]]))

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

(defrecord Pixel [style p]
  Drawable
  (draw [_ ctx]
    (canvas/pixel ctx style p)))

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

;; Debugging record for comparing against direct canvas manipulations.
(defrecord Raw []
  Drawable
  (draw [_ ctx]
    (doto (.-ctx ctx)
      (.moveTo 100 100)
      (.lineTo 400 400)
      (.stroke))))

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

(defn with-style [style & shapes]
  (Shape. style shapes))

(defn shape [children]
  (Shape. {} children))

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
