(ns canvas-demos.db
  (:require [canvas-demos.examples.ex1 :as ex1]))

(defonce current-drawing #'ex1/picture)

(defonce window (atom {:zoom 1 :offset [0 0] :width 0 :height 0 }))
