(ns canvas-demos.db
  (:require [canvas-demos.canvas-utils :as canvas]
            [canvas-demos.examples.ex1 :as ex1]))

;;;;; State

(defonce current-drawing (atom #'ex1/house))

;;;; Canvas mutations

(def var-table
  {:ex1 #'ex1/picture
   :house #'ex1/house
   })

(defn switch! [sym]
  (reset! current-drawing (get var-table sym)))
