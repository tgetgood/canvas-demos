(ns canvas-demos.db
  (:require [reagent.core :as reagent]))

(defonce window (reagent/atom {:zoom 1 :offset [0 0] :width 0 :height 0}))
