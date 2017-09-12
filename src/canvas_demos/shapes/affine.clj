(ns canvas-demos.shapes.affine)

(defmacro with-origin [shape origin & body]
  `(let [[x# y#] ~origin]
     (-> ~shape
         (translate (- x#) (- y#))
         ~@body
         (translate x# y#))))
