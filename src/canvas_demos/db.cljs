(ns canvas-demos.db
  (:require [fipp.clojure :as fipp]
            [reagent.core :as reagent]))

(defonce window (reagent/atom {:zoom 1 :offset [0 0] :width 0 :height 0}))

(defonce client-width (reagent/atom 0))

(defonce editor-content (reagent/atom (with-out-str (fipp/pprint 243))))

(defonce canvas (reagent/atom []))

(defonce drawings (reagent/atom {}))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;; State Mutation
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
