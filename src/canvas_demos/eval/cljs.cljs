(ns canvas-demos.eval.cljs
  (:require [cljs.js :as cljs])
  (:require-macros [canvas-demos.eval.cljs :refer [load-sources]]))

(def eval-ns (create-ns 'canvas-demos.eval))

(def source-map
  (load-sources canvas-demos.examples.ex1
                canvas-demos.shapes.base
                ))
;;;
(defn retrieve-source [source-map ns macro?]
  (when-let [sources (get source-map ns)]
    (if macro?
      (or (:clj sources) (:cljc sources))
      (or (:cljs sources) (:cljc sources)))))

(defn load-fn [{:keys [name macros path]} cb]
  (if-let [src (retrieve-source source-map name macros)]
    (cb {:lang :clj
         :source src})
    (cb nil)))

(defn eval [form]
  (cljs/eval (cljs/empty-state)
             form
             {:ns         eval-ns
              :source-map true
              :load load-fn
              :eval       cljs/js-eval}
             (fn [{:keys [error value]}]
               (when error
                 (throw (js/Error. error)))
               value)))

(eval
 '(ns canvas-demos.eval
    (:require [canvas-demos.examples.ex1 :refer [house]]
              [canvas-demos.shapes.base :refer [circle line rectangle]])))

(defn set-current-drawing! [name]
  (when-let [code (get @drawings name)]
    (reset! editor-content (with-out-str (fipp/pprint code)))
    (when-let [shape (eval code)]
      (reset! canvas shape))))





(defn s! []
  (cljs/eval (cljs/empty-state)
             '(+ 1 4)
             {:ns (find-ns-obj 'canvas-demos.db)
              :source-map true
              :eval cljs/js-eval }
             (fn [{:keys [value error]}]
               (when error
                 (println error))
               value)))
