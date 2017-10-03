(ns canvas-demos.canvas
  "Wrapper around HTML Canvas elements with a stateless API. All stateful canvas
  setters are replaced by a style map. As compatible with manual canvas
  manipulation as manual canvas manipulation is with itself."
  (:require [clojure.string :as string])
  (:require-macros [canvas-demos.canvas :refer [with-stroke with-style]]))

;;;;; Protocol

(defprotocol ICanvas
  ;; TODO: Presumably I should wrap the entire canvas API.
  ;;
  ;; Well, that's an interesting question. The canvas API doesn't have circles,
  ;; but I want a circle. It has rectangles, but I can make rectangles from
  ;; lines. I am designing a new language whether I like it or not, so let's
  ;; take that responsibility head on.
  (clear [this])
  (apply-affine-tx [this atx])
  (set-affine-tx [this atx] "Set the current affine tx matrix")
  (pixel [this style p])
  (line [this style p q])
  (rectangle [this style p q]
    "Rectangle defined by bottom left and top right corners")
  (circle [this style centre radius]))

;;;; Styling Logic

(def style-keys
  [:stroke-style
   :line-join
   :line-cap
   :miter-limit
   :line-width
   :line-join ; round, bevel, mitre
   :line-cap ; round, square, butt
   :line-dash
   ])

;; TODO: Gradients
;; TODO: Text

(defn clj->jsm [k]
  (let [bits (string/split (name k) #"-")]
    (apply str (first bits) (map string/capitalize (rest bits)))))

(defn- save-style [ctx]
  (into {:fill (.-fillStyle ctx)}
        (map (fn [k]
               [k (aget ctx (clj->jsm k))])
          style-keys)))

(defn- set-style! [ctx style]
  (doseq [[k v] (dissoc style :fill)]
    (when v
      ;;FIXME: Really weird state problems.
      ;; ???
      (aset ctx (clj->jsm k) v)))

  ;; Treat fill specially for convenience
  (when-let [fill (:fill style)]
    (set! (.-fillStyle ctx) fill)))

;;; Impl

(deftype Canvas [elem ctx]
  ICanvas
  (clear [_]
    (let [width (.-clientWidth elem)
          height (.-clientHeight elem)]
      (.clearRect ctx 0 0 width height)))
  (apply-affine-tx [_ [a b c d e f]]
    (.transform ctx a b c d e f))
  (set-affine-tx [_ [a b c d e f]]
    (.setTransform ctx a b c d e f))
  (pixel [_ style [x y]]
    (with-style ctx style
      (.moveTo ctx x y)
      (.fillRect ctx x y 1 1)))
  (line [_ style [x1 y1] [x2 y2]]
    (with-style ctx style
      (with-stroke ctx
        (.moveTo ctx x1 y1)
        (.lineTo ctx x2 y2))))
  (rectangle [_ style [x1 y1] [x2 y2]]
    (with-style ctx style
      (with-stroke ctx
        (.moveTo ctx x1 y1)
        (.rect ctx x1 y1 (- x2 x1) (- y2 y1)))))
  (circle [_ style [x y] r]
    (with-style ctx style
      (with-stroke ctx
        (.moveTo ctx (+ r x) y)
        (.arc ctx x y r 0 (* 2 js/Math.PI))))))

(defn context
  "Returns a Canvas object wrapping the given HTML canvas dom element."
  [elem]
  (let [ctx (.getContext elem "2d")]
    (Canvas. elem ctx)))
