(ns canvas-demos.subs
  (:require [re-frame.core :as re-frame]
            [canvas-demos.db :as db]
            [canvas-demos.drawing :as drawing]))

(def drawing drawing/drawing)

(re-frame/reg-sub
 :canvas
 (fn [_]
   drawing))

(re-frame/reg-sub
 :stroke
 (fn [db]
   (get-in db db/stroke)))

(re-frame/reg-sub
 :window
 (fn [db]
   (get-in db db/window)))
