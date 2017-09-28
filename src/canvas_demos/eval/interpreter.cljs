(ns canvas-demos.eval.interpreter
  (:require [canvas-demos.examples.ex1 :as ex1]
            [canvas-demos.eval.cljs :as cljs-eval]
            [cljs.tools.reader :as reader]
            [clojure.walk :as walk]
            [canvas-demos.shapes.affine :refer [translate]]
            [canvas-demos.shapes.base :as base :refer [circle line rectangle]]))

(def test-fn  '(fn [x] (line [x 100] [1000 1000])))

(declare eval)

(defn syms-in-fn-body [form]
  (let [syms     (transient #{})
        arg-syms (into #{} (second form))]
    (walk/prewalk (fn [f]
                    (when (symbol? f)
                      (conj! syms f))
                    f)
                  (rest form))
    (into [] (apply disj (persistent! syms) arg-syms))))

(defn eval-fn [form env]
  (let [unbound (syms-in-fn-body form)
        wrapper (cons 'fn (list unbound form))
        fn-obj  (cljs-eval/eval wrapper)]
    (apply fn-obj (map #(eval % env) unbound))))

(defn builtins []
  (reduce (fn [acc obj]
            (doseq [k (js/Object.keys obj)]
              (aset acc k (aget obj k)))
            acc)
          #js {}
          (map find-ns-obj
            '[cljs.core
              canvas-demos.shapes.affine
              canvas-demos.shapes.base
              canvas-demos.examples.ex1])))


(defn resolve* [sym env]
  (if-let [impl (aget env (munge (name sym)))]
    impl
    (if (= sym 'house)
      ex1/house
      (throw (js/Error. (str (name sym) " cannot be resolved"))))))


(defn eval [form & [env]]
  (let [env (or env (builtins))]
    (cond
      (and (list? form) (contains? #{'fn* 'fn} (first form)))
      (eval-fn form env)

      (list? form)
      (apply (eval (first form) env) (map #(eval % env) (rest form)))

      (symbol? form)
      (resolve* form env)

      (vector? form)
      (mapv eval form)

      :else
      form)))
