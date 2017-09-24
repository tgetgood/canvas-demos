(ns canvas-demos.db
  (:require [canvas-demos.examples.ex1 :as ex1]
            [fipp.clojure :refer [pprint]]
            [reagent.core :as reagent]))

(defonce window (reagent/atom {:zoom 1 :offset [0 0] :width 0 :height 0}))

(def text (reagent/atom (with-out-str (pprint ex1/picture))))
