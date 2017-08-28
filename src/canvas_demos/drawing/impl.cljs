(ns canvas-demos.drawing.impl
  (:require [canvas-demos.canvas :as canvas]))

(defn invert
  "Converts normal plane coordinates (origin in the bottom left) to standard
  computer graphics coordinates (origin in the top left)"
  [[x y] h]
  [x (- h y)])

;; TODO: This custom coordinate inversion for each shape is a hack. Really we
;; should have a vector type which can be inverted, define shapes in terms of
;; vectors, and then walk all data being rendered...
(defmulti invert-coords (fn [w t] (:type t)))

(defmethod invert-coords :default
  [_ r]
  (.error js/console (str "I can't invert a " (:type r)))
  r)

(defmethod invert-coords :rectangle
  [wh r]
  (-> r
      (update :p invert wh)
      (update :h -)))

(defmethod invert-coords :line
  [h l]
  (-> l
      (update :p invert h)
      (update :q invert h)))

(defmethod invert-coords :circle
  [h c]
  (update c :c invert h))

;;;;; Drawing Logic

(defn classify [x] (:type x))

(defmulti draw* (fn [ctx shape] (classify shape)))

(defmethod draw* :default
  [ctx shape]
  ;; TODO: This is a question. "I don't know what to do." should be rephrased
  ;; everywhere as "What should I do?".
  (.error js/console (str "I don't know how to draw a " (classify shape))))

(defmethod draw* nil
  [_ _]
  (.error js/console "I can't draw something without a :type."))

(defmethod draw* :circle
  [ctx {:keys [c r style]}]
  (canvas/circle ctx style c r))

(defmethod draw* :line
  [ctx {:keys [style p q]}]
  (canvas/line ctx style p q))

(defmethod draw* :rectangle
  [ctx {:keys [style p w h]}]
  (canvas/rectangle ctx style p (mapv + p [w 0] [0 h])))

;;;;; Drawing

(defn draw! [ctx content]
  (let [[_ h] (canvas/canvas-container-dimensions)]
    (canvas/clear ctx)
    (doseq [shape (map (partial invert-coords h) content)]
      (draw* ctx shape))))

;;;;; Animation

(defn raf [f]
  (.requestAnimationFrame js/window f))

(def ^:private animation-frames (atom nil))

(defn- animate* [ctx]
  (let [[frame & more] @animation-frames]
    (when frame
      (draw! ctx frame)
      (swap! animation-frames rest)
      (raf #(animate* ctx)))))

(defn animate! [frames]
  (reset! animation-frames frames)
  (animate* (canvas/context (canvas/canvas-elem))))

(defn kill-animation! []
  (reset! animation-frames nil))
