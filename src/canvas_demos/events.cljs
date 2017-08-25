(ns canvas-demos.events
  (:require [canvas-demos.canvas :as canvas]
            [canvas-demos.db :as db]
            [re-frame.core :as re-frame]
            [canvas-demos.subs :as subs]
            [canvas-demos.drawing :as drawing]))

;;;;; Events

(re-frame/reg-event-db
 ::initialize-db
 (fn  [_ _]
   db/default-db))

(re-frame/reg-event-fx
 ::resize-canvas
 (fn [{db :db [_ canvas] :event}]
   (let [[width height :as dim] (canvas/canvas-container-dimensions)
         offset                 (canvas/canvas-container-offset)
         window                 (get-in db db/window)]
     (array-map
      :db              (update-in db db/window assoc
                                  :width width :height height :offset offset)
      ::resize-canvas! [canvas dim]
      ::redraw-canvas! [(canvas/context canvas) window]))))

(re-frame/reg-event-fx
 ::redraw-canvas
 (fn [{[_ elem] :event db :db}]
   (let [ctx    (canvas/context elem)
         window (get-in db db/window)]
     {::redraw-canvas! [ctx window]})))

;;;;; FX

(re-frame/reg-fx
 ::redraw-canvas!
 (fn [[ctx window]]
   (drawing/draw! ctx (map (partial drawing/invert-coords window) drawing/drawing))))

(re-frame/reg-fx
 ::resize-canvas!
 (fn [[canvas dimensions]]
   (canvas/set-canvas-size! canvas dimensions)))
