(ns canvas-demos.canvas
  "Wrapper around HTML Canvas elements with a stateless API. All stateful canvas
  setters are replaced by a style map. As compatible with manual canvas
  manipulation as manual canvas manipulation is with itself."
  (:require [clojure.string :as string])
  (:require-macros [canvas-demos.canvas :refer [with-stroke with-style]]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;; Stateless Styling
;;
;; TODO: Shadows
;; TODO: Text
;; TODO: ImageData (pixel manipulation)
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def style-abbrevs
  (-> (make-hierarchy)
      (derive :stoke-style :stroke)
      (derive :fill-style  :fill)))

(defn key-tx [k]
  (let [bits (string/split (name k) #"-")]
    (apply str (first bits) (map string/capitalize (rest bits)))))

(defn- val-tx [v]
  (if (keyword? v)
    (name v)
    v))

(defmulti set-style* (fn [ctx k v] k) :hierarchy #'style-abbrevs)

(defmethod set-style* :default
  [ctx k v]
  (aset ctx (key-tx k) (val-tx v)))

(defmethod set-style* :line-dash
  [ctx k v]
  (.setLineDash ctx (clj->js v)))

(defn linear? [spec]
  (and (= (type spec) PersistentVector) (= 2 (count spec))))

(defn linear-gradient [ctx [x0 y0] [x1 y1]]
  (.createLinearGradient ctx x0 y0 x1 y1))

(defn radial-gradient [ctx {[x0 y0] :c r0 :r} {[x1 y1] :c r1 :r}]
  (.createRadialGradient ctx x0 y0 r0 x1 y1 r1))

(defn- create-gradient [ctx {:keys [from to stops]}]
  (let [gradient (if (linear? from)
                   (linear-gradient ctx from to)
                   (radial-gradient ctx from to))]
    (doseq [[k v] stops]
      (.addColorStop gradient k (name v)))
    gradient))

(defn- maybe-gradient [ctx v]
  (if-let [grad (:gradient v)]
    (create-gradient ctx grad)
    (name v)))

(defmethod set-style* :stroke
  [ctx _ v]
  (aset ctx "strokeStyle" (maybe-gradient ctx v)))

(defmethod set-style* :fill
  [ctx _ v]
  (aset ctx "fillStyle" (maybe-gradient ctx v)))

(defn- set-style! [ctx style]
  (doseq [[k v] style]
    (set-style* ctx k v)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;; Canvas Wrapper
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defprotocol ICanvas
  ;; TODO: Presumably I should wrap the entire canvas API.
  ;;
  ;; Well, that's an interesting question. The canvas API doesn't have circles,
  ;; but I want a circle. It has rectangles, but I can make rectangles from
  ;; lines. I am designing a new language whether I like it or not, so let's
  ;; take that responsibility head on.
  (clear [this] "Restore the canvas to its initial state.")

  (apply-affine-tx [this atx] "Multiply the current tx by atx")
  (set-affine-tx [this atx] "Set the current affine tx matrix")

  (pixel [this style point]
    "Style a single pixel. Actually a square of width and height 1px whose
    bottom left corner is the pixel in question")
  (line [this style from to] "Draw a line")
  (rectangle [this style bottom-left top-right]
    "Rectangle defined by bottom left and top right corners")
  (circle [this style centre radius] "Draws a circle"))

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
    ;; REVIEW: Experimental feature. Subjectively makes a small improvement. How
    ;; can I test that empirically?
    (set! (.-imageSmoothingEnabled ctx) true)
    (Canvas. elem ctx)))
