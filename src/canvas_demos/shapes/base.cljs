(ns canvas-demos.shapes.base
  (:refer-clojure :exclude [val])
  (:require [canvas-demos.canvas :as canvas :include-macros true]
            [canvas-demos.shapes.protocols :refer [draw Drawable scalar val vec2]]
            [canvas-demos.shapes.style :as style]))

(defrecord Line [style from to]
  Drawable
  (draw [_ ctx]
    (canvas/line ctx (style/unwrap style) (val from) (val to))))

(defrecord Circle [style centre radius]
  Drawable
  (draw [_ ctx]
    (canvas/circle ctx (style/unwrap style) (val centre) (val radius))))

(defrecord Rectangle [style bottom-left width height]
  Drawable
  (draw [_ ctx]
    (let [bottom-left (val bottom-left)
          top-right (mapv + bottom-left [(val width) 0] [0 (val height)])]
      (canvas/rectangle ctx (style/unwrap style) bottom-left top-right))))

(defrecord Pixel [style p]
  Drawable
  (draw [_ ctx]
    (canvas/pixel ctx (style/unwrap style) p)))

(defrecord Shape [style content]
  Drawable
  (draw [_ ctx]
    (canvas/with-style (.-ctx ctx) (style/unwrap style)
      (draw content ctx))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;; API
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn with-style [style & shapes]
  (Shape. (style/wrap style) shapes))

(defn shape [children]
  (Shape. {} children))

(defn line
  ([{:keys [style from to]}]
   (line style from to))
  ([from to]
   (line {} from to))
  ([style from to]
   (Line. (style/wrap style) (vec2 from) (vec2 to))))

(defn rectangle
  ([{:keys [style bottom-left width height]}]
   (rectangle style bottom-left width height))
  ([bottom-left width height]
   (rectangle {} bottom-left width height))
  ([style bottom-left width height]
   (Rectangle. (style/wrap style)
               (vec2 bottom-left)
               (scalar width)
               (scalar height))))

(defn circle
  ([{:keys [style centre radius]}]
   (circle style centre radius))
  ([centre radius]
   (circle {} centre radius))
  ([style centre radius]
   (Circle. (style/wrap style) (vec2 centre) (scalar radius))))
