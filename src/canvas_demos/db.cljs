(ns canvas-demos.db
  (:require [canvas-demos.eval.interpreter :as interpreter]
            [canvas-demos.examples.ex1 :as ex1]
            [cljs.tools.reader :as reader]
            [fipp.clojure :as fipp]
            [paren-soup.core :as ps]
            [reagent.core :as reagent]
            [reagent.ratom :as ratom :include-macros true]))

(defonce paren-soup (atom nil))

(defonce window (reagent/atom {:zoom 1 :offset [0 0] :width 0 :height 0 }))

(defonce current-drawing (reagent/atom "ex1"))

(defonce code
  (reagent/atom {:selected "ex1"
                 :drawings {"house" ex1/house
                            "ex1"   ex1/picture
                            "blank" '[]}}))

(def canvas
  (ratom/reaction
   (let [{:keys [drawings selected]} @code]
     (interpreter/eval (get drawings selected)))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;; Editor
;;;;;
;;;;; TODO: This stuff does not belong in this namespace.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn current-edit [edit-history]
  (let [{:keys [current-state states]} edit-history]
    (get states current-state)))

(defn editor-content []
  (current-edit @(.-edit-history @paren-soup)))

(defn update-from-editor! [_ _ old editor]
  (let [current (current-edit editor)]
    (when-not (= (:text current) (:text (current-edit old)))
      (try
        (when-let [form (reader/read-string (:text current))]
          (swap! code update :drawings assoc (:selected @code) form))
        (catch js/Error e (println e))))))

(defn update-editor-content [content]
  (ps/edit-and-refresh! @paren-soup (assoc (editor-content) :text content)))

(defn disconnect-editor! []
  (remove-watch (.-edit-history @paren-soup) :editor))

(defn connect-editor! []
  (add-watch (.-edit-history @paren-soup) :editor update-from-editor!))

(defn init-editor! []
  (let [ed-elem (js/document.getElementById "editor")
        {:keys [drawings selected]} @code]
    (reset! paren-soup (ps/init ed-elem #js {}))
    (->  (get drawings selected)
         fipp/pprint
         with-out-str
         update-editor-content)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;; State Mutation
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn set-current-drawing! [name]
  (let [{:keys [drawings selected]} @code]
    (when-let [form (get drawings selected)]
      (swap! code assoc :selected name)
      (update-editor-content (with-out-str (fipp/pprint form))))))
