(ns canvas-demos.interpreter
  (:require canvas-demos.shapes.base
            canvas-demos.shapes.affine
            [cljs.js :as cljs]
            [clojure.walk :as walk]))

(def test-fn  '(fn [x] (line [x 100] [1000 1000])))

(defn compile [form]
  (cljs/eval (cljs/empty-state)
             form
             {:eval cljs/js-eval
              :context :expr}
             (fn [{:keys [error value] :as result}]
               (when error
                 (throw error))
               value)))

(declare eval*)

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
        fn-obj  (compile wrapper)]
    (apply fn-obj (map (partial eval* env) unbound))))

(defn js->clj*
  "Special processing for ns objects. I don't know why this is needed."
  ;; HACK:!!
  [obj]
  (try
    (reduce (fn [acc k] (assoc acc k (aget obj k)))
            {} (.keys js/Object obj))
    (catch js/Error e {})))

(defn builtins []
  (reduce merge
         (map (comp js->clj* find-ns-obj)
           '[cljs.core
             canvas-demos.shapes.affine
             canvas-demos.shapes.base])))

(defn resolve* [sym {:keys [builtins bindings] :as env}]
  ;; TODO: We're currently obliterating namespaces. That's easy for now, but not
  ;; good for the long term.
  (let [n (munge (name sym))]
    (cond
      (contains? builtins n) (get builtins n)
      (contains? bindings n) (eval* env (get bindings n))
      :else
      (throw (js/Error. (str n " cannot be resolved"))))))


(defn- eval* [env form]
  (cond
    (and (list? form) (contains? #{'fn* 'fn} (first form)))
    (eval-fn form env)

    (list? form)
    (apply (eval* env (first form)) (map (partial eval* env) (rest form)))

    (symbol? form)
    (resolve* form env)

    (vector? form)
    (mapv (partial eval* env) form)

    :else
    form))


(defn eval [form & [bindings]]
  (let [env {:builtins (builtins) :bindings (or bindings {})}]
    (eval* env form)))
