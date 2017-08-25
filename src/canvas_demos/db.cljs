(ns canvas-demos.db)

(def default-db
  {:canvas {}
   :window {:origin [0 0]
            :zoom 1}
   :stroke {}})

(def window [:window])

(def canvas [:canvas])

(def stroke [:stroke])
(def stroke-pos (conj stroke :current))
(def stroke-start (conj stroke :start))
(def stroke-end (conj stroke :end))

(defn new-stroke [db pos]
  (assoc-in db stroke {:start pos}))
