(ns canvas-demos.shapes.affine
  (:require [canvas-demos.drawing :as drawing]))

(defrecord AffineWrapper [atx base-shape]
  drawing/Drawable
  (draw [_ ctx]
    (let [before-atx (.-currentTransform ctx)]
      (.transform ctx atx)
      (draw base-shape ctx)
      (.setTransform ctx before-atx))))
