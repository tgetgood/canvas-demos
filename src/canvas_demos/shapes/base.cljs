(ns canvas-demos.shapes.base
  (:refer-clojure :exclude [val])
  (:require [canvas-demos.canvas :as canvas]
            [canvas-demos.drawing :as drawing :refer [scalar val vec2]]
            [clojure.walk :as walk]))

(defn scalarise-style [style]
  (walk/postwalk #(if (number? %) (scalar %) %) style))

(defn descalarise-style [style]
  (walk/postwalk #(if (instance? drawing/Scalar %) (val %) %) style))

(defrecord Line [style p q]
  drawing/Drawable
  (draw [_ ctx]
    (canvas/line ctx (descalarise-style style) (val p) (val q))))

(defrecord Circle [style c r]
  drawing/Drawable
  (draw [_ ctx]
    (canvas/circle ctx (descalarise-style style) (val c) (val r))))

(defrecord Rectangle [style p w h]
  drawing/Drawable
  (draw [_ ctx]
    (let [p (val p)
          q (mapv + p [(val w) 0] [0 (val h)])]
      (canvas/rectangle ctx (descalarise-style style) p q))))

(defrecord Pixel [style p]
  drawing/Drawable
  (draw [_ ctx]
    (canvas/pixel ctx (descalarise-style style) p)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;; API
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn line
  ([{:keys [style p q]}]
   (line style p q))
  ([p q]
   (line {} p q))
  ([style p q]
   (Line. (scalarise-style style) (vec2 p) (vec2 q))))

(defn rectangle
  ([{:keys [style p w h]}]
   (rectangle style p w h))
  ([p w h]
   (rectangle {} p w h))
  ([style p w h]
   (Rectangle. (scalarise-style style) (vec2 p) (scalar w) (scalar h))))

(defn circle
  ([{:keys [style c r]}]
   (circle style c r))
  ([c r]
   (circle {}  c r))
  ([style c r]
   (Circle. (scalarise-style style) (vec2 c) (scalar r))))
