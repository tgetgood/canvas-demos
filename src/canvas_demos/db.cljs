(ns canvas-demos.db
  (:require [reagent.core :as reagent]
            [cljs.js :as cljs]
            [fipp.clojure :as fipp]))

(defonce window (reagent/atom {:zoom 1 :offset [0 0] :width 0 :height 0}))

(defonce editor-content (reagent/atom (with-out-str (fipp/pprint 243))))

(defonce canvas (reagent/atom []))

(defonce drawings (reagent/atom {}))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;; State Mutation
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


(defn set-current-drawing! [name]
  (when-let [code (get @drawings name)]
    (reset! editor-content (with-out-str (fipp/pprint code)))
    (cljs/eval-str (cljs/empty-state)
                   (str
                    '(ns canvas-demos.G_14
                       (:require
            [canvas-demos.shapes.base :as base :refer [circle line rectangle]]))
                    "\n"
                    code)
                   'the-code
               {:source-map true
                :eval cljs/js-eval}
               (fn [{:keys [error value]}]
                 (when error
                   (println error))
                 (reset! canvas value)))))



(defn s! []
  (cljs/eval (cljs/empty-state)
             `(+ 1 4)
             {:source-map true
              :eval cljs/js-eval }
             (fn [{:keys [value error]}]
               (when error
                 (println error))
               value)))
