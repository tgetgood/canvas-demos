(ns canvas-demos.db
  (:require [canvas-demos.interpreter :as interpreter]
            [canvas-demos.examples.ex1 :as ex1]
            [cljs.tools.reader :as reader]
            [fipp.clojure :as fipp]
            [paren-soup.core :as ps]
            [reagent.core :as reagent]
            [reagent.ratom :as ratom :include-macros true]))

(defonce paren-soup (atom nil))

(defonce window (reagent/atom {:zoom 1 :offset [0 0] :width 0 :height 0 }))

(defonce selected (reagent/atom "blank"))

;; If this is defonce, we can edit from the browser and not lose our changes. If
;; this is just def, we can edit from an editor and see the changes in the
;; browser. Need both at different times.

(def drawings
  (reagent/atom {"house" ex1/house
                 "ex1"   ex1/picture
                 "blank" '[]}))

(def code
  (ratom/reaction
   (get @drawings @selected)))

(def canvas
  (ratom/reaction
   (interpreter/eval @code @drawings)))

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
          (swap! drawings assoc @selected form))
        (catch js/Error e nil)))))

(defn update-editor-content! [content]
  (ps/edit-and-refresh! @paren-soup
                        (->> content
                             fipp/pprint
                             with-out-str
                             (assoc (editor-content) :text))))

(defn disconnect-editor! []
  (remove-watch (.-edit-history @paren-soup) :editor))

(defn connect-editor! []
  (add-watch (.-edit-history @paren-soup) :editor update-from-editor!))

(defn init-editor! []
  (let [ed-elem (js/document.getElementById "editor")]
    (reset! paren-soup (ps/init ed-elem #js {}))
    (update-editor-content! @code)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;; State Mutation
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn set-current-drawing! [name]
  (when (contains? @drawings name)
    (reset! selected name)
    (update-editor-content! @code)))
