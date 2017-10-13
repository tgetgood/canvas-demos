(ns canvas-demos.canvas
  "Wrapper around HTML Canvas elements with a stateless API. All stateful canvas
  setters are replaced by a style map. As compatible with manual canvas
  manipulation as manual canvas manipulation is with itself."
  (:require [clojure.string :as string])
  (:require-macros [canvas-demos.canvas :refer
                    [with-single-stroke with-connected-stroke with-style]]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;; Stateless Styling
;;
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
;;
;; TODO: Text
;; Single line works, still need formatted blocks
;; TODO: Shadows
;; TODO: ImageData (pixel manipulation)
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defprotocol ICanvas
  (clear [this] "Restore the canvas to its initial state.")

  (clear-state! [this] "Wipe the path state")
  (get-state [this] "Returns the Current path state")
  (set-state! [this state] "Set the path state")

  (apply-affine-tx [this atx] "Multiply the current tx by atx")
  (set-affine-tx [this atx] "Set the current affine tx matrix")

  (measure-text [this text] "Returns text metric object for text")
  (text [this style text position] "Draw text at position")
  (line [this style from to] "Draw a line")
  (rectangle [this style bottom-left top-right]
    "Rectangle defined by bottom left and top right corners")
  (arc [this style centre radius from to])
  (circle [this style centre radius] "Draw a circle"))

(deftype Canvas [elem ctx
                 ;; REVIEW: Should I reify and use atoms, or is this good form?
                 ;; More potential to screw up this way, that's for sure.
                 ^:mutable __point
                 ^:mutable __path-start]
  ICanvas
  (clear [_]
    (let [width (.-clientWidth elem)
          height (.-clientHeight elem)]
      (.clearRect ctx 0 0 width height)))

  (get-state [_]
    [__point __path-start])
  (clear-state! [_]
    (set! __point nil)
    (set! __path-start nil))
  (set-state! [_ [p s]]
    (set! __point p)
    (set! __path-start s))

  (apply-affine-tx [_ [a b c d e f]]
    (.transform ctx a b c d e f))
  (set-affine-tx [_ [a b c d e f]]
    (.setTransform ctx a b c d e f))

  (measure-text [_ text]
    (.measureText ctx))
  (text [_ style text [x y]]
    (with-style ctx style
      (.fillText ctx text x y)))

  (line [_ style [x1 y1 :as from] [x2 y2 :as to]]
    (if (empty? style)
      (with-connected-stroke ctx from to
        (.lineTo ctx x2 y2))
      (with-style ctx style
        (with-single-stroke ctx
          (.moveTo ctx x1 y1)
          (.lineTo ctx x2 y2)
          (set! __path-start nil)))))

  (rectangle [_ style [x1 y1] [x2 y2]]
    (with-style ctx style
      (with-single-stroke ctx
        (.rect ctx x1 y1 (- x2 x1) (- y2 y1)))))

  (arc [_ style [x y] r from to]
    (let [rad (fn [d] (* d js/Math.PI (/ 1 180)))
          from (rad from)
          to (rad to)]
      (if (empty? style)
        (let [c1 (* r (js/Math.cos from))
              s1 (* r (js/Math.sin from))
              c2 (* r (js/Math.cos to))
              s2 (* r (js/Math.sin to))]
          (with-connected-stroke ctx [(+ x c1) (+ y s1)] [(+ x c2) (+ y s2)]
            (.arc ctx x y r from to)))
        (with-style ctx style
          (with-single-stroke ctx
            (.arc ctx x y r from to))))))

  (circle [_ style [x y] r]
    (with-style ctx style
      (with-single-stroke ctx
        (.arc ctx x y r 0 (* 2 js/Math.PI))))))

(defn context
  "Returns a Canvas object wrapping the given HTML canvas dom element."
  [elem]
  (let [ctx (.getContext elem "2d")]
    ;; REVIEW: Experimental feature. Subjectively makes a small improvement. How
    ;; can I test that empirically?
    (set! (.-imageSmoothingEnabled ctx) true)
    (->Canvas elem ctx nil nil)))
