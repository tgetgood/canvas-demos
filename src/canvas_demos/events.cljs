(ns canvas-demos.events
  (:require [canvas-demos.canvas :as canvas]
            [clojure.string :as string]))

(defonce window (atom {:zoom 1 :offset [0 0]}))

(def ^:private drag-state (atom nil))

(defn kw->js [kw]
  (string/replace (name kw) #"-" ""))

(defn c-space-point [e]
  (let [[_ h] (canvas/canvas-container-dimensions)]
    [(js/parseInt (.-x e)) (- h (js/parseInt (.-y e)))]))

(defn normalise-zoom [dz]
  (let [base js/Math.E
        scale 100]
    (js/Math.pow base (/ (- dz) scale))))

(defn c-space->r-space
  [[x y]]
  (let [{z :zoom [ox oy] :offset} @window]
    [(/ (- x ox) z) (/ (- y oy) z)]))

(defn zoom-c [dz ox zx]
  (+ (* dz ox) (* zx (- 1 dz))))

(defn update-zoom [{z :zoom o :offset} p dz]
  (let [zc p]
    {:zoom (* z dz)
     :offset (mapv (partial zoom-c z dz) o zc)}))

(defn update-offset [{:keys [zoom] :as w} [dx dy]]
  (update w :offset
          (fn [[x y]]
            [(- x dx) (- y dy)])))

(def handlers
  {:mouse-down (fn [e]
                 (reset! drag-state (c-space-point e)))
   :click (fn [e]
            (.log js/console (c-space->r-space (c-space-point e))))
   :mouse-up   (fn [e]
                 (reset! drag-state nil))
   :mouse-move (fn [e]
                 (when @drag-state
                   (let [q     (c-space-point e)
                         p     @drag-state
                         delta (mapv - p q)]
                     (reset! drag-state q)
                     (swap! window update-offset delta))))
   :wheel      (fn [e]
                 (let [p  (c-space-point e)
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
