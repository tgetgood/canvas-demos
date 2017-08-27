(ns canvas-demos.drawing.impl
  (:require [canvas-demos.canvas :as canvas]
            [canvas-demos.window :as window]))

(defmulti invert-coords (fn [w t] (:type t)))

(defmethod invert-coords :default
  [w r]
  (.error js/console (str "I can't invert a " (:type r)))
  r)

(defmethod invert-coords :rectangle
  [w r]
  (-> r
      (update :p window/invert w)
      (update :h -)))

(defmethod invert-coords :line
  [w l]
  (-> l
      (update :p window/invert w)
      (update :q window/invert w)))

(defmethod invert-coords :circle
  [w c]
  (update c :c window/invert w))

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

;;;;; Entry

(defn draw! [ctx window content]
  (canvas/clear ctx)
  (doseq [shape (map (partial invert-coords window) content)]
    (draw* ctx shape)))
