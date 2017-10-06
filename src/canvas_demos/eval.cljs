(ns canvas-demos.eval
  (:require [eval-soup.core :as eval-soup]))

(def evaluation-ns 'cljs.user)

(def eval-ns
  (do (create-ns 'canvas-demos.eval.evaluation)
      (atom 'canvas-demos.eval.evaluation)))

(def require-form
  '(:require [canvas-demos.shapes.affine
              :refer [translate rotate scale reflect with-origin]]
             [canvas-demos.shapes.base :refer [line circle rectangle]]))

(defn- prepend-path [opts]
  (update opts :path #(str "/js/compiled/out/" %)))

(defn load-fn [opts cb]
  (if (re-matches #"^goog/.*" (:path opts))
    (eval-soup/custom-load! (-> opts
                                (update :path eval-soup/fix-goog-path)
                                prepend-path)
                            [".js"]
                            cb)
    (eval-soup/custom-load! (prepend-path opts)
                            (if (:macros opts)
                              [".clj" ".cljc"]
                              [".cljs" ".cljc" ".js"])
                            cb)))

(defn eval
  [forms cb]
  (let [forms (if (vector? forms) forms [forms])
        read-cb (fn [results]
                  (eval-soup/eval-forms
                   (eval-soup/add-timeouts-if-necessary
                    forms results)
                   cb
                   eval-soup/state
                   eval-ns
                   load-fn))
        init-cb (fn [results]
                  (eval-soup/eval-forms
                   (map eval-soup/wrap-macroexpand forms)
                   read-cb
                   eval-soup/state
                   eval-ns
                   load-fn))]
    (eval-soup/eval-forms
     ['(ns cljs.user)
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
     load-fn)))
