(ns canvas-demos.drawing
  (:require [canvas-demos.canvas :as canvas]
            [canvas-demos.window :as window]))

(def drawing
  [{:type :rectangle
    :p [10 20]
    :w 100
    :h 1000}])

(defmulti invert-coords (fn [w t] (:type t)))

(defmethod invert-coords :rectangle
  [w r]
  (-> r
      (update :p window/invert w)
      (update :h -)))

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

(defn draw! [ctx content]
  (canvas/clear ctx)
  (doseq [shape content]
    (draw* ctx shape)))
