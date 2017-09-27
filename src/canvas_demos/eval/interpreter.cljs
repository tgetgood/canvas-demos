(ns canvas-demos.eval.interpreter
  (:require [canvas-demos.examples.ex1 :as ex1]
            [cljs.tools.reader :as reader]
            [canvas-demos.shapes.base :as base :refer [circle line rectangle]]))

(def builtins
  (apply merge
         (map (comp js->clj find-ns-obj)
           '[cljs.core
             canvas-demos.shapes.affine
             canvas-demos.shapes.base])))

(defn lookup-fn [sym env]
  (if-let [impl (get env (munge (name sym)))]
    impl
    (throw (js/Error. (str (name sym) " cannot be resolved")))))

(defn eval [form & [env]]
  (let [env (or env builtins)]
    (cond
      (and (list? form) (symbol? (first form)))
      (apply (lookup-fn (first form) env) (map eval (rest form)))

      (vector? form)
      (mapv eval form)

      :else
      form)))
