(ns canvas-demos.shapes.base)

(defmacro dd
  "Takes a seq of raw canvas commands (without the canvas object) and returns
  an object which implements Drawable. When drawn, the commands will be invoked
  as (doto ctx ~@body)"
  [body]
  `(reify
     canvas-demos.shapes.protocols/Drawable
     (~'draw [_# ctx#]
       (doto (.-ctx ctx#)
         ~@body))))
