(ns cljs-todos.core
  (:require [rum.core :as rum]
						[cljs-todos.actions :refer [state boundActions]]
						[cljs-todos.components :refer [app]]
						[cljs-todos.routes]
						[nightlight.repl-server]))

(defn render
  ([key ref previousState state] (render state))
  ([state]
   (rum/mount
    (app state boundActions)
    (. js/document (getElementById "app")))))

(defn updateLocalStorage [key ref previousState state]
	(js/localStorage.setItem "state" (js/JSON.stringify (clj->js state))))

;; reset state to saved state if exists
(let [savedState (js->clj (js/JSON.parse (js/localStorage.getItem "state")) :keywordize-keys true)]
	(if savedState  (reset! state savedState)))

(add-watch state :rerender render)
(add-watch state :localstorage updateLocalStorage)
(render @state)
(defn on-js-reload [] (render @state))
