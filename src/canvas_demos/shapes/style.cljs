(ns canvas-demos.shapes.style
  "Helpers to vectorise the style parameters."
  (:refer-clojure :exclude [val])
  (:require [canvas-demos.shapes.protocols :refer [constant scalar val vec2]]
            [clojure.walk :as walk]))

(defn- escape-stops [m]
  (into {} (map (fn [[k v]] [(constant k) v]) m)))

(declare escape)

(defmulti process-map (fn [[k v]] k))

(defmethod process-map :default
  [[k v]]
  [k (escape v)])

(defmethod process-map :stops
  [[k v]]
  [k (escape-stops v)])

(defmethod process-map :line-dash
  [[k v]]
  [k (mapv escape v)])

(defn- escape [form]
  (cond
    (map? form)                             (into {} (map process-map form))
    ;; HACK: Special case for probable vectors...
    (and (vector? form) (= 2 (count form))) (vec2 form)
    (vector? form)                          (mapv escape form)
    (number? form)                          (scalar form)
    :else                                   form))

;;;;; External API

(defn wrap [style]
  (escape style))

(defn unwrap [style]
  (walk/postwalk val style))
