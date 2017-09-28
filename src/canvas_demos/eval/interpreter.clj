(ns canvas-demos.eval.interpreter)

(defmacro eval-fn [form]
  `(let [args# (second form)]
     `(fn ~args# 6)))
