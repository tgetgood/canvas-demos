(ns canvas-demos.events
  (:require [canvas-demos.canvas :as canvas]
            [clojure.string :as string]))

(defonce window (atom {:zoom 1 :offset [0 0]}))

(def ^:private drag-state (atom nil))

(defn kw->js [kw]
  (string/replace (name kw) #"-" ""))

(defn c-space-point [e]
  [(js/parseInt (.-x e)) (js/parseInt (.-y e))])

(defn normalise-zoom [dz]
  (let [base js/Math.E
        scale 100]
    (js/Math.pow base (/ (- dz) scale))))

(defn c-space->r-space
  [[x y]]
  (let [{z :zoom [ox oy] :offset} @window]
    [(- (/ x z) ox) (- (/ y z) oy)]))

(defn r-norm
  [[x y]]
  (let [[_ h] (canvas/canvas-container-dimensions)
        {z :zoom [ox oy] :offset} @window]
    [(- (/ x z) ox) (- (/ (- h y) z) oy)]))

(defn homothetic
  "Applies homothetic tx centred at p with scale z to point q"
  [p q z dz]
  (let [f (fn [z dz px qx] (+ (* z qx) (* (- 1 z) px)))]
    (mapv (partial f z dz) p q)))

(defn update-zoom [{z :zoom  q :offset} p dz]
  {:zoom (* z dz)
   :offset (homothetic p q dz)})

(defn update-offset [{:keys [zoom] :as w} [dx dy]]
  (update w :offset
          (fn [[x y]]
            [(- x dx) (+ y dy)])))

(def handlers
  {:mouse-down (fn [e]
                 (reset! drag-state (c-space-point e)))
   :click (fn [e]
            (.log js/console (r-norm (c-space-point e))))
   :mouse-up   (fn [e]
                 (reset! drag-state nil))
   :mouse-move (fn [e]
                 (when @drag-state
                   (let [q     (c-space-point e)
                         p     @drag-state
                         delta (apply mapv -
                                      (map c-space->r-space [p q]))]
                     (reset! drag-state q)
                     (swap! window update-offset delta))))
   :wheel      (fn [e]
                 (let [p  (c-space->r-space (c-space-point e))
                       dz (normalise-zoom (js/parseInt (.-deltaY e)))]
                   (swap! window update-zoom p dz)))})


(defonce registered-listeners (atom nil))

(defn register-handlers! [elem]
  (reset! registered-listeners handlers)
  (doseq [[event cb] @registered-listeners]
    (.addEventListener elem (kw->js event) cb)))

(defn remove-handlers! [elem]
  (doseq [[event cb] @registered-listeners]
    (.removeEventListener elem (kw->js event) cb)))
