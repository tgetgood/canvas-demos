(ns canvas-demos.worker)

(.log js/console "w!")
;; (enable-console-print!)

(defn return [x]
  (js/postMessage x))

(defn arg [e]
  (js->clj (.-data e)))

(defn on-message [e]
  (.log js/console (.-data e))
  (return (apply * (js->clj (.-data e))))
  )

(set! js/onmessage on-message)
