(ns canvas-demos.drawing
  (:refer-clojure :exclude [val])
  (:require [canvas-demos.canvas :as canvas]
            [canvas-demos.events :as events]
            [clojure.walk :as walk]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;; Protocols
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

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

(defprotocol Valuable
  (val [this]))

(extend-protocol Valuable
  default
  (val [this] this))

(defprotocol Projectable
  (project [this window]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;; Projection Types
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defrecord Vector [x y]
  Projectable
  (project [this {z :zoom [ox oy] :offset}]
    (Vector. (+ ox (* z x)) (+ oy (* z y))))

  Valuable
  (val [_] [x y]))

(defrecord Scalar [s]
  Projectable
  (project [_ {z :zoom}]
    (Scalar. (* z s)))

  Valuable
  (val [_] s))

(defn vec2
  ([[x y]] (vec2 x y))
  ([x y] (Vector. x y)))

(defn scalar [s]
  (Scalar. s))

(defn project-all [picture window]
  (walk/prewalk
   (fn [o]
     (if (satisfies? Projectable o)
       (project o window)
       o))
   picture))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;; Main Draw
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn draw!
  "Walks content recursively and draws each shape therein in a preorder
  traversal order. Later draws occlude earlier draws."
  [content & [project?]]
  (let [[_ h]        (canvas/canvas-container-dimensions)
        ctx          (canvas/context (canvas/canvas-elem))
        window       @events/window]
    (canvas/clear ctx)
    ;; Use a fixed Affine tx to normalise coordinates.
    ;; REVIEW: Does resetting this on each frame hurt performance?
    (canvas/set-affine-tx ctx [1 0 0 -1 0 h])
    (draw (if project?
            (project-all content window)
            content)
          ctx)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;; Animation
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;; FPS calc

(let [frame-counter (atom 0)
      prev (atom (.getTime (js/Date.)))]
  (defn count-frame []
    (swap! frame-counter inc)
    (let [now (.getTime (js/Date.))
          elapsed (/ (- now @prev) 1000)]
      (when (< 5 elapsed)
        (.log js/console (str "FPS: " (int (/ @frame-counter elapsed))))
        (reset! frame-counter 0)
        (reset! prev now)))))

;;;;; Actual animation

(defn raf [f]
  (.requestAnimationFrame js/window f))

(defonce ^:private animation-frames (atom nil))

(defn- animate* [windowed?]
  (count-frame)
  (let [[frame & more] @animation-frames]
    (when frame
      (draw! frame windowed?)
      (swap! animation-frames rest)
      (raf #(animate* windowed?)))))

(defn animate! [frames & [windowed?]]
  ;; HACK: By waiting 2 animation frames, I can make sure that a nil
  ;; animation-frames atom actually has time to kill all running animations
  ;; before this resets it.
  (raf #(raf (fn []
               (reset! animation-frames frames)
               (animate* windowed?)))))

(defn stop-animation! []
  (reset! animation-frames nil))

(defn animating?
  "Returns true if an animation is currently being played."
  []
  (boolean @animation-frames))
