(ns canvas-demos.examples.presentation
  (:require [canvas-demos.canvas-utils :as canvas-utils]
            [canvas-demos.examples.stateful :as stateful]
            [canvas-demos.shapes.affine :refer [scale translate]]
            [canvas-demos.shapes.base
             :refer
             [circle line rectangle textline wipe-hack with-style]]
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
    [(scale code-box 500 box-height)
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
  (let [words (remove empty? (string/split (string/replace t #"\n" " ") #" "))]
    (map-indexed (fn [i ts]
                   (text (apply str ts) [x (- y (* 18 (inc i)))]))
                 (chunk-text words 65))))

(defn title [text]
  (let [w (* 12 (count text))]
    (textline {:font "30px serif"} text [(- 640 (/ w 2)) 660])))

;;;;; First slide

(def state-1
  [slide-box
   (title "Abstract Drawings as Functions")

   (textbox
    "Let's say we've made nice functions to draw a black circle and a red circle:"
    [100 550])
   (translate d1 100 250)
   (text "Simple, right?" [100 200])
   (text "Let's see what happens..." [100 160])

   (translate call1 700 550)
   (translate stateful/circle 750 450)
   (translate stateful/red-circle 950 450)

   (textbox
    "So far so good. Now let's say we refactor and the order in which we call
    the fns get's swapped."
     [700 380])

   (translate call2 700 270)
   (translate stateful/red-circle 950 200)
   (translate stateful/circle 750 200)

   (textbox "Why are they both red? What happened?" [700 100])])

;;;;; Slide 2

(def state-2
  [slide-box

   (title "Solution? Lots of Guard Code")

   (textbox "Canvas has built in save and restore methods which push and pop the
   global state from a stack. This allows us to guard our code from our own
   modifications." [100 550])

   (translate (set-code "
const safeRedCircle = ctx => {
  ctx.save()
  ctx.strokeStyle = 'red'
  ctx.beginPath()
  ctx.arc(300, 100, 50, 0, 2 * Math.PI)
  ctx.endPath()
  ctx.stroke()
  ctx.restore()
}") 100 300)

   (translate (set-code "safeRedCircle(ctx)\ncircle(ctx)") 700 500)
   (translate stateful/safe-red-circle 900 400)
   (translate stateful/circle 750 400)

   (textbox "Perfect!" [700 300])])

;;;; slide 3

(def state-3
  [slide-box
   (title "But You Can't Control the World")

   (textbox "But now suppose we get a library fn dottedCircle:" [100 550])
   (translate
    (set-code "
dottedCircle = ctx => {
  ctx.setLineDash([3])
  ctx.strokeStyle = 'green'
  ctx.arc(200, 300, 50, 0, 2 * Math.PI)
  ctx.stroke()")
    100 400)
   (translate
    [(line [50 0] [50 0])
     stateful/dotted-circle
     wipe-hack]
    200 300)

   (textbox "Well, it's green, but still potentially useful." [100 200])

   (translate (set-code "dottedCircle(ctx)\nsafeRedCircle(ctx)\ncircle(ctx)")
              700 550)

   (line [900 480] [900 480])
   (translate stateful/dotted-circle 850 480)
   (translate stateful/safe-red-circle 750 380)
   (translate stateful/circle 950 380)
   wipe-hack

   (textbox "Umm, what if we put it at the end?" [1000 470])

   (translate (set-code "safeRedCircle(ctx)\ncircle(ctx)\ndottedCircle(ctx)")
              700 230)

   (translate stateful/safe-red-circle 750 60)
   (translate stateful/circle 950 60)
   (translate stateful/dotted-circle 850 160)

   (textbox "Nevermind..." [1050 100])])

;;;;; Benefits of Data

(def data-shape
  (set-code "
(def circle
  {::type ::circle
   ::style {}
   ::centre [0 0]
   ::radius 50})

(def red-circle
  (assoc circle ::style {:stroke-style :red}))

(def dotted-circle
  (assoc circle ::style {:line-dash [10]
                         :stroke-style :green})) "))

(def shape-call
  (set-code "
[(assoc dotted-circle ::centre [200 300])
 (assoc red-circle ::centre [300 100])
 (assoc circle ::centre [100 100])]"))

(def data-benefits
  [slide-box
   (title "Abstracting Drawings as Data")

   (textbox "What if instead, we represent shapes by data?" [100 550])

   (translate data-shape 100 250)

   (translate shape-call 700 400)

   (circle {:style {:line-dash [10]
                    :stroke :green}
            :centre [850 300]
            :radius 50})
   (circle {:style {:stroke :red}
            :centre [950 200]
            :radius 50})
   (circle {:centre [750 200]
            :radius 50})

   (textbox "Was that so bad?" [700 100])])

;;;; Desires Slide

(defn point [text p]
  (textline {:font "20px sans serif"} (str "• " text) p))

(def what-do-we-want
  [slide-box

   (title "Properties of a Less Painful Graphics Language (My Opinions)")

   (point "Stateless (subdrawings are completely independent)" [300 500])
   (point "Shapes are Data" [300 450])
   (point "Immediate Mode (at all levels)" [300 400])
   (point "Compositing as Concatenation" [300 350])
   (point "Minimize Arbitrary Non Intuitives" [300 300])])

;;;; Theory slide

(def theory-of
  [slide-box

   (title "Basic Ideas")

   (point "The basic unit is the path (lines, beziers, etc.)" [300 500])
   (point "Paths can be composed (joined end to end)" [300 450])
   (point "Paths can be composited (overlayed one on the other)" [300 400])
   (point "Styles can be applied to any path, or group of paths" [300 350])
   (point "Deepest nesting wins (this might be a mistake)" [300 300])
   (point "Affine Transformations as a first class compositing mechanism" [300 250])
   ])

(defn default-zoom []
  (let [[w h] (canvas-utils/canvas-container-dimensions)]
    (min (/ w 1280) (/ h 720))))

(def show
  [{:zoom (default-zoom) :offset [0 0] :slide state-1}
   {:zoom (default-zoom) :offset [-1500 0] :slide state-2}
   {:zoom (default-zoom) :offset [-3000] :slide state-3}
   {:zoom (default-zoom) :offset [0 1000] :slide what-do-we-want}
   {:zoom (default-zoom) :offset [-1500 1000] :slide data-benefits}
   {:zoom (default-zoom) :offset [-3000 1000] :slide theory-of}])

(def go
  (map (fn [{z :zoom [x y] :offset s :slide}]
         (translate (or s []) (- x) (- y)))
    show))

(def pres [#'show #'go])
