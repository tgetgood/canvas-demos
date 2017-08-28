(ns canvas-demos.events
  (:require-macros [cljs.core.async.macros :refer [go-loop]])
  (:require [canvas-demos.canvas :as canvas]
            [canvas-demos.db :as db]
            [cljs.core.async :as async]
            [re-frame.core :as re-frame]
            [canvas-demos.subs :as subs]
            [canvas-demos.drawing :as drawing]))

;;;;; Debounced events

(defn debouncer
  "Takes a t timeout and function f, and returns a function which when called
  invokes f at most once every t ms. The return value of f is thrown away.
  Intended for async event handlers."
  [t f]
  (let [ch (async/chan (async/sliding-buffer 1))]
    (go-loop []
      (when-let [v (async/<! ch)]
        (f v)
        (async/<! (async/timeout t))
        (recur)))
    (fn [v]
      (async/put! ch v))))

(def resize-canvas-debounced
  (debouncer 500 #(re-frame/dispatch [::resize-canvas %])))

;;;;; Events

(re-frame/reg-event-db
 ::initialize-db
 (fn  [_ _]
   db/default-db))

(re-frame/reg-event-fx
 ::resize-canvas
 (fn [{db :db [_ elem] :event}]
   (let [[width height :as dim] (canvas/canvas-container-dimensions)
         offset                 (canvas/canvas-container-offset)
         window                 (get-in db db/window)]
     (array-map
      :db              (update-in db db/window assoc
                                  :width width :height height :offset offset)
      ;; We want this on the next event loop cycle, not actually delayed.
      :dispatch-later [{:ms 0 :dispatch [::redraw-canvas elem]}]
      ::resize-canvas! [elem dim]))))

(re-frame/reg-event-fx
 ::redraw-canvas
 (fn [{[_ elem content] :event db :db}]
   (let [window (get-in db db/window)]
     {::redraw-canvas! [elem window content]})))

(re-frame/reg-event-db
 ::start-animation
 (fn [db [_ frames]]
   (assoc db :frames frames)))

(re-frame/reg-event-db
 ::stop-animation)

(re-frame/reg-event-fx
 ::frame
 (fn [{db :db}]
   (merge
    {:db (update db :frames next)}
    (when-let [frame (first (:frames db))]
      {::redraw-canvas! }))))

;;;;; FX

(re-frame/reg-fx
 ::redraw-canvas!
 (fn [[elem window]]
   (drawing/draw! (canvas/context elem) window drawing/drawing)))

(re-frame/reg-fx
 ::resize-canvas!
 (fn [[canvas dimensions]]
   (canvas/set-canvas-size! canvas dimensions)))
