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
             {:eval cljs/js-eval
              :context :expr}
             (fn [{:keys [error value] :as result}]
               (when error
                 (throw error))
               value)))

(defn s! []
  (cljs/eval (cljs/empty-state)
             '(fn [] 6)
             {:ns eval-ns
              :source-map true
              :eval cljs/js-eval }
             (fn [{:keys [value error]}]
               (when error
                 (println error)))))
