(ns canvas-demos.db
  (:require [canvas-demos.eval.interpreter :as interpreter]
            [canvas-demos.examples.ex1 :as ex1]
            [cljs.tools.reader :as reader]
            [fipp.clojure :as fipp]
            [paren-soup.core :as ps]
            [reagent.core :as reagent]
            [reagent.ratom :as ratom :include-macros true]))

(defonce paren-soup (atom nil))

(defonce window (reagent/atom {:zoom 1 :offset [0 0] :width 0 :height 0}))

(defonce client-width (reagent/atom 0))

(defonce current-drawing (reagent/atom "ex1"))

(defonce drawings (reagent/atom {"ex1" ex1/picture
                                 "blank" '()}))

(def canvas
  (ratom/reaction (interpreter/eval (get @drawings @current-drawing))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;; State Querying
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn editor-content []
  (let [{:keys [current-state states]} @(.-edit-history @paren-soup)]
    (get states current-state)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;; State Mutation
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn update-editor-content [content]
  (ps/edit-and-refresh! @paren-soup (assoc (editor-content) :text content)))

(defn set-current-drawing! [name]
  (when-let [code (get @drawings name)]
    (update-editor-content (with-out-str (fipp/pprint code)))))

(defn update-editor! [text]
  (reset! editor-content text)
  (try
    (when-let [code (reader/read-string text)]
      (swap! drawings assoc @current-drawing code))
    (catch js/Error e nil)))
