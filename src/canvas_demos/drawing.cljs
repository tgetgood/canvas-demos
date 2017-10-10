(ns canvas-demos.drawing
  (:require [canvas-demos.canvas :as canvas]
            [canvas-demos.canvas-utils :as canvas-utils]
            [canvas-demos.db :as db]
            [canvas-demos.shapes.protocols :as protocols]
            [clojure.walk :as walk]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;; Projection
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn project-all [picture window]
  (walk/prewalk
   (fn [o]
     (if (satisfies? protocols/Projectable o)
       (protocols/project o window)
       o))
   picture))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;; Main Draw
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn draw!
  "Walks content recursively and draws each shape therein in a preorder
  traversal order. Later draws occlude earlier draws."
  [content & [window]]
  (let [[_ h]        (canvas-utils/canvas-container-dimensions)
        ctx          (canvas/context (canvas-utils/canvas-elem))]
    (canvas/clear ctx)
    ;; Use a fixed Affine tx to normalise coordinates.
    ;; REVIEW: Does resetting this on each frame hurt performance?
    (canvas/set-affine-tx ctx [1 0 0 -1 0 h])
    (protocols/draw (if window
            (project-all content window)
            content)
          ctx)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;; Hacky Global Redraw
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn redraw! []
  (draw! @db/current-drawing @db/window))

(def var-table
  {:ex1 #'canvas-demos.examples.ex1/picture})

(defn switch! [sym]
  (set! db/current-drawing (get var-table sym))
  (redraw!))

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
      (draw! frame (when windowed? @db/window))
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
