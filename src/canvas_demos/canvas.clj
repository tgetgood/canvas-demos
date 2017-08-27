(ns canvas-demos.canvas
  (:require [clojure.string :as string]))

(defmacro with-stroke
  "Wraps body in boilerplate code for strokes on canvas and executes."
  [ctx fill? & body]
  `(do
     (.beginPath ~ctx)
     ~@body
     (.closePath ~ctx)
     (.stroke ~ctx)))

(defmacro with-style
  "Saves current global draw state, sets up global draw state according to
  style, executes body, then restores global draw state as if this never
  happened.
  Very impure function that lets the rest of the program be a big more pure."
  [ctx style & body]
  `(let [old# (save-style ~ctx)]
     (set-style! ~ctx ~style)
     ~@body
     (when (and (contains? ~style :fill) (:fill ~style))
       (.fill ~ctx))
     (set-style! ~ctx old#)
     ;; We don't want to arbitrarily return the last property set.
     nil))
