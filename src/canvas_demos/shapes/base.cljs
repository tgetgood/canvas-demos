(ns canvas-demos.shapes.base
  (:refer-clojure :exclude [val])
  (:require [canvas-demos.canvas :as canvas :include-macros true]
            [canvas-demos.shapes.protocols :refer [draw Drawable scalar val vec2]]
            [canvas-demos.shapes.style :as style]))

(defrecord Line [style from to]
  Drawable
  (draw [_ ctx]
    (canvas/line ctx (style/unwrap style) (val from) (val to))))

(defrecord Circle [style centre radius]
  Drawable
  (draw [_ ctx]
    (canvas/circle ctx (style/unwrap style) (val centre) (val radius))))

(defrecord Rectangle [style bottom-left width height]
  Drawable
  (draw [_ ctx]
    (let [bottom-left (val bottom-left)
          top-right (mapv + bottom-left [(val width) 0] [0 (val height)])]
      (canvas/rectangle ctx (style/unwrap style) bottom-left top-right))))

(defrecord Pixel [style p]
  Drawable
  (draw [_ ctx]
    (canvas/pixel ctx (style/unwrap style) p)))

(defrecord TT [style xs]
  Drawable
  (draw [_ ctx]
    (let [ictx (.-ctx ctx)]
      (canvas/with-style ictx (style/unwrap style)
        (draw xs ctx)

        ))))

(defrecord T2 [style xs]
  Drawable
  (draw [_ ctx]
    (canvas/with-style (.-ctx ctx) {:line-width 10}
      (doto (.-ctx ctx)
        (.beginPath)
        (.moveTo 100 100)
        (.lineTo 100 100)
        (.lineTo 400 100)
        (.lineTo 250 300)
        (.closePath)
        (.stroke)))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;; API
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn line
  ([{:keys [style from to]}]
   (line style from to))
  ([from to]
   (line {} from to))
  ([style from to]
   (Line. (style/wrap style) (vec2 from) (vec2 to))))

(defn rectangle
  ([{:keys [style bottom-left width height]}]
   (rectangle style bottom-left width height))
  ([bottom-left width height]
   (rectangle {} bottom-left width height))
  ([style bottom-left width height]
   (Rectangle. (style/wrap style)
               (vec2 bottom-left)
               (scalar width)
               (scalar height))))

(defn circle
  ([{:keys [style centre radius]}]
   (circle style centre radius))
  ([centre radius]
   (circle {} centre radius))
  ([style centre radius]
   (Circle. (style/wrap style) (vec2 centre) (scalar radius))))

(defn tt [] #_(T2. 1 2) (TT. (style/wrap {:line-width 10
                                          :line-join :round
                      })

                    [(line [100 100] [400 100])
                     (line [400 100] [250 300])
                     (line [250 300] [100 100])

                     (line [600 600] [800 650])
                     (line [800 650] [600 650])
                     (line [600 650] [750 750])
                     (line {:line-width 6
                            :stroke :purple} [750 750] [600 600])
                     ]))
