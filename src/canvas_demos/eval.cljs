(ns canvas-demos.eval
  (:require [eval-soup.core :as eval-soup]))

(def evaluation-ns 'cljs.user)

(def eval-ns (atom 'cljs.user))

(def require-form
  '(:require [canvas-demos.shapes.affine :refer [translate rotate scale reflect with-origin]]
             [canvas-demos.shapes.base :refer [line circle rectangle]]))

(defn eval
  ([form cb]
   (eval form nil cb))
  ([form name cb]
   (let [forms (if (nil? name) [form] [(list 'def name form) name])
         read-cb (fn [results]
                   (eval-soup/eval-forms (eval-soup/add-timeouts-if-necessary
                                          forms results)
                                         cb
                                         eval-soup/state
                                         eval-ns
                                         eval-soup/custom-load!))
         init-cb (fn [results]
                   (eval-soup/eval-forms (map eval-soup/wrap-macroexpand forms)
                                         read-cb
                                         eval-soup/state
                                         eval-ns
                                         eval-soup/custom-load!))]
     (eval-soup/eval-forms ['(ns cljs.user)
                            '(def ps-last-time (atom 0))
                            '(defn ps-reset-timeout! []
                               (reset! ps-last-time (.getTime (js/Date.))))
                            '(defn ps-check-for-timeout! []
                               (when (> (- (.getTime (js/Date.)) @ps-last-time) 5000)
                                 (throw (js/Error. "Execution timed out."))))
                            '(set! *print-err-fn* (fn [_]))
                            (list 'ns @eval-ns require-form)]
                           init-cb
                           eval-soup/state
                           eval-ns
                           eval-soup/custom-load!))))
