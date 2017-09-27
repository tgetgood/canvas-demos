(ns canvas-demos.examples
  (:require [canvas-demos.examples.ex1 :as ex1]
            [canvas-demos.examples.ex2 :as ex2]
            [canvas-demos.examples.mandelbrot :as mandelbrot]
            [canvas-demos.drawing :as drawing]
            [canvas-demos.events :as events]
            canvas-demos.core
            [canvas-demos.examples.ex3 :as ex3]))

(def ex-map
  "Examples. Note that the draw functions here do not reload properly
  themselves. These draw functions should call drawing/draw! on another var
  which contains the data. When that var gets modified. the correct thing
  happens. I don't know why the top level fn won't reload."
  {;:ex1 (drawing/draw! ex1/picture)
   :ex2 ex2/start!
   :ex3 ex3/draw!
   :mandelbrot mandelbrot/draw!})

(defn switch
  "Sets the current development example to be ex. See ex-map for listing."
  [ex]
  (when-let [f (get ex-map ex)]
    (reset! canvas-demos.core/main f)
    (canvas-demos.core/refresh-app! f)))

(defn freeze
  "Makes image currently on screen completely static. It will no longer respond
  to interaction. Code reloading is also disabled."
  []
  (reset! canvas-demos.core/main nil))
