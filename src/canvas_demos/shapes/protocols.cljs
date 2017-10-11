(ns canvas-demos.shapes.protocols)

(defprotocol Drawable
  (draw [this ctx]))

(extend-protocol Drawable
  default
  (draw [this _]
    (.error js/console (str "I don't know how to draw a " (type this))))

  nil
  (draw [_ _]
    (.error js/console "Can't draw a nil.")))
