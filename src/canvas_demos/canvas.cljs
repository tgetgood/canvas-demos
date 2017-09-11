(ns canvas-demos.canvas
  "Wrapper around HTML Canvas elements with a stateless API. All stateful canvas
  setters are replaced by a style map. As compatible with manual canvas
  manipulation as manual canvas manipulation is with itself."
  (:require [clojure.string :as string]
            [canvas-demos.events :as events])
  (:require-macros [canvas-demos.canvas :refer [with-style with-stroke]]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;; Canvas Manipulation
;;
;; Best way I could find to dynamically get the correct dimensions of the canvas
;; element was to put it in a div with 100% height and width and then query that
;; div at runtime.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn canvas-elem []
  (.getElementById js/document "canvas"))

(defn canvas-container []
  (.getElementById js/document "canvas-container"))

(defn canvas-container-dimensions []
  [(.-clientWidth (canvas-container))
   (.-clientHeight (canvas-container))])

(defn set-canvas-size! [canvas [width height]]
  (set! (.-width canvas) width)
  (set! (.-height canvas) height))

(defn canvas-container-offset []
  (let [c (canvas-container)]
    [(.-offsetLeft c) (.-offsetTop c)]))

(defn fullscreen-canvas! []
  (let [[w h :as dim] (canvas-container-dimensions)]
    (set-canvas-size! (canvas-elem) dim)
    (swap! events/window assoc :width w :height h)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;; Canvas Wrapper
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;;;; Protocol

(defprotocol ICanvas
  ;; TODO: Presumably I should wrap the entire canvas API.
  (clear [this])
  (apply-affine-tx [this atx])
  (set-affine-tx [this atx] "Set the current affine tx matrix")
  (pixel [this style p])
  (line [this style p q])
  (rectangle [this style p q]
    "Rectangle defined by bottom left and top right corners")
  (circle [this style centre radius]))

(def style-keys
  [:stroke-style
   :line-join
   :line-cap
   :miter-limit
   :line-width])

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

(defn context [elem]
  (let [ctx (.getContext elem "2d")]
    (Canvas. elem ctx)))
