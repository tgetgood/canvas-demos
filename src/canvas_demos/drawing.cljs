(ns canvas-demos.drawing
  (:require [canvas-demos.canvas :as canvas]))

(defprotocol Drawable
  (draw [this ctx]))

(extend-protocol Drawable
  default
  (draw [this _]
    (.error js/console (str "I don't know how to draw a " (type this))))

  nil
  (draw [_ _]
    (.error js/console "Can't draw a nil."))

  ;; HACK: Treat vectors as drawings. How hacky is this really?
  cljs.core/PersistentVector
  (draw [this ctx]
    (doseq [s this]
      (draw s ctx))))

;;;;; Drawing

(defn draw! [content]
  (let [[_ h] (canvas/canvas-container-dimensions)
        ctx   (canvas/context (canvas/canvas-elem))]
    (canvas/clear ctx)
    ;; Use a fixed Affine tx to normalise coordinates.
    ;; REVIEW: Does resetting this on each frame hurt performance?
    (canvas/atx ctx 1 0 0 -1 0 h)
    (draw content ctx)))

;;;;; Animation

;;; FPS calc

(let [frame-counter (atom 0)
      prev (atom (.getTime (js/Date.)))]
  (defn count-frame []
    (swap! frame-counter inc)
    (let [now (.getTime (js/Date.))
          elapsed (/ (- now @prev) 1000)]
      (when (< 5 elapsed)
        (reset! frame-counter 0)
        (reset! prev now)
        (.log js/console (str "FPS: " (int (/ @frame-counter elapsed))))))))


(defn raf [f]
  (.requestAnimationFrame js/window f))

(defonce ^:private animation-frames (atom nil))

(defn- animate* []
  (let [[frame & more] @animation-frames]
    (count-frame)
    (when frame
      (draw! frame)
      (swap! animation-frames rest)
      (raf #(animate*)))))

(defn animate! [frames]
  (reset! animation-frames frames)
  (animate*))

(defn stop-animation! []
  (reset! animation-frames nil))
