(ns canvas-demos.shapes.protocols)

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
