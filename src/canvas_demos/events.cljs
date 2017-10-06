(ns canvas-demos.events
  (:require [canvas-demos.canvas-utils :as canvas]
            [canvas-demos.db :as db :refer [window]]
            [clojure.string :as string]))

(def ^:private drag-state (atom nil))

;;;;; Pan and Zoom logic

(defn c-space->r-space
  "Inverse of Projectable/project for vectors. Currently only used for
  debugging."
  [[x y]]
  (let [{z :zoom [ox oy] :offset} @window]
    [(/ (- x ox) z) (/ (- y oy) z)]))

(defn c-space-point
  "Returns Inverted pixel coordinates (origin in the lower left) of the event
  e."
  [e]
  (let [h (:height @window)
        [ox oy] (canvas/canvas-container-offset)]
    [(- (.-clientX e) ox) (- h (- (.-clientY e) oy))]))

(defn normalise-zoom [dz]
  (let [base js/Math.E
        scale 100]
    (js/Math.pow base (/ (- dz) scale))))

(defn zoom-c [dz ox zx]
  (+ (* dz ox) (* zx (- 1 dz))))

(defn update-zoom [{z :zoom o :offset :as w} zc dz]
  (assoc w
         :zoom (* z dz)
         :offset (mapv (partial zoom-c dz) o zc)))

(defn update-offset [{:keys [zoom] :as w} [dx dy]]
  (update w :offset
          (fn [[x y]]
            [(- x dx) (- y dy)])))

;;;;; Drawing
;; Brace yourself

(def current-draw (atom {}))

(defn init-draw [type point])
(defn update-draw [point])

;;;;; Handlers

(def handlers
  {:mouse-down (fn [e]
                 (reset! drag-state (c-space-point e))
                 (when-not (= :grab @db/input-mode)
                   (init-draw @db/input-mode @drag-state)))
   :mouse-up   (fn [e]
                 (reset! drag-state nil))
   :mouse-move (fn [e]
                 (when @drag-state
                   (let [q     (c-space-point e)
                         p     @drag-state
                         delta (mapv - p q)]
                     (reset! drag-state q)
                     (if (= :grab @db/input-mode)
                       (swap! window update-offset delta)
                       (update-draw @drag-state)))))
   :wheel      (fn [e]
                 (let [p  (c-space-point e)
                       dz (normalise-zoom (js/parseInt (.-deltaY e)))]
                   (swap! window update-zoom p dz)))})

(def canvas-event-handlers
  (into {}
        (map (fn [[k v]]
               [(keyword (str "on-" (name k))) v])
          handlers)))

;;;;; Handler Registration

(defn kw->js [kw]
  (string/replace (name kw) #"-" ""))

(defonce registered-listeners (atom nil))

(defn register-handlers! [elem]
  (reset! registered-listeners handlers)
  (doseq [[event cb] @registered-listeners]
    (.addEventListener elem (kw->js event) cb)))

(defn remove-handlers! [elem]
  (doseq [[event cb] @registered-listeners]
    (.removeEventListener elem (kw->js event) cb)))
