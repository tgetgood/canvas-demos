(ns canvas-demos.eval.cljs
  (:require [clojure.java.io :as io]
            [clojure.string :as string]))

(defn read-file [path t]
  (when-let [f (io/resource (str path "." (name t)))]
    (slurp f)))

(defn read-source [ns]
  (let [path (-> ns
                 (string/replace #"\." "/")
                 (string/replace #"-" "_"))]
    (into {}
          (map (fn [k] [k (read-file path k)])
            [:clj :cljs :cljc]))))

(defmacro load-sources [& nses]
  (into {}
        (map (fn [ns]
               [`(quote ~ns) (read-source ns)])
          nses)))
