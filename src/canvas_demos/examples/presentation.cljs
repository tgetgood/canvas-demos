(ns canvas-demos.examples.presentation
  (:require [canvas-demos.canvas-utils :as canvas-utils]
            [canvas-demos.examples.stateful :as stateful]
            [canvas-demos.shapes.affine :refer [scale translate]]
            [canvas-demos.shapes.base :refer [line rectangle textline with-style]]
            [clojure.string :as string]))

(defn screen-box [dim]
  (let [[w h] dim]
    (rectangle {:fill "rgba(0,0,0,0)"} [0 0] w h)))

(defn fullscreen-box []
  (screen-box (canvas-utils/canvas-container-dimensions)))

(def slide-box
  [(screen-box [1280 720])
   ;; HACK: Broken styling fix
   (line [0 0] [0 0])])

(defn vcentre [x]
  (let [w (or (:width (meta x)) 100)]
    )
  )

(def code-box
  (rectangle {:style {:fill "#E1E1E1"
                      :stroke "rgba(0,0,0,0)"}
              :bottom-left [0 0]
              :width 1
              :height 1}))

(defn set-code [s]
  (let [lines       (string/split s #"\n")
        line-height 16
        box-height  (* (inc (count lines)) line-height)]
    [(scale code-box 400 box-height)
     (line {:stroke :black} [30 0] [30 box-height])
     (with-style {:font "14px monospace"}
       (map-indexed (fn [i line]
                      (let [h (- box-height (* line-height (inc i)))]
                        [(textline (str (inc i)) [5 h] )
                         (textline line [35 h])]))
                    lines))]))

(def d1
  (set-code "
const circle = ctx => {
  ctx.beginPath()
  ctx.arc(100, 100, 50, 0, 2 * Math.PI)
  ctx.endPath()
  ctx.stroke()
}

const redCircle = ctx => {
  ctx.strokeStyle = 'red'
  ctx.beginPath()
  ctx.arc(300, 100, 50, 0, 2 * Math.PI)
  ctx.endPath()
  ctx.stroke()
}
"))

(def call1
  (set-code "circle(ctx)\nredCircle(ctx)"))

(def call2 (set-code "redCircle(ctx)\ncircle(ctx)"))

(defn text [t p]
  (textline {:font "14px sans serif"} t p))

(defn chunk-text [words chars-per-line]
  (loop [[w & more :as words] words
         chunks []
         chunk ""]
    (if (empty? words)
      (map string/trim (conj chunks chunk))
      (if (< chars-per-line (count chunk))
        (recur words (conj chunks chunk) "")
        (recur more chunks (str chunk " " w))))))

(defn textbox [t [x y]]
  (let [words (string/split t #" ")]
    (map-indexed (fn [i ts]
                   (text (apply str ts) [x (- y (* 18 (inc i)))]))
                 (chunk-text words 70))))

(def state-1
  [slide-box
   (textline {:font "30px serif"} "Abstract Drawings with Functions" [400 660])

   (textbox "Let's say we've made nice functions to draw a black circle and a red circle:"
            [100 550])
   (translate d1 100 250)
   (text "Simple, right?" [100 200])
   (text "Let's see what happens..." [100 160])

   (translate call1 700 550)
   (translate stateful/circle 750 450)
   (translate stateful/red-circle 950 450)

   (textbox
    "So far so good. Now let's say we refactor and the order in which we call the fns get's swapped."
     [700 380])

   (translate call2 700 270)
   (translate stateful/red-circle 950 200)
   (translate stateful/circle 750 200)

   (textbox "Why are they both red? What happened?" [700 100])
   ])

(def state-2
  [slide-box])

(def state-3
  [slide-box])

(defn point [text p]
  (textline {:font "20px sans serif"} (str "• " text) p))

(def what-do-we-want
  [slide-box

   (textline {:font "30px serif"} "Properties of an Ideal Graphics Language (My Opinions)" [250 660])

   (point " " [200 500])])

(defn default-zoom []
  (let [[w h] (canvas-utils/canvas-container-dimensions)]
    (min (/ w 1280) (/ h 720))))

(def show
  [{:zoom (default-zoom) :offset [0 0] :slide state-1}
   {:zoom (default-zoom) :offset [1500 0] :slide state-2}
   {:zoom (default-zoom) :offset [3000] :slide state-3}
   {:zoom (default-zoom) :offset [0 1000] :slide what-do-we-want}
   {:zoom (default-zoom) :offset [1500 1000]}])

(def go
  (map (fn [{z :zoom [x y] :offset s :slide}]
         (translate (or s []) (- x) (- y)))
    show))

(def pres [#'show #'go])
