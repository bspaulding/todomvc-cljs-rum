(ns cljs-todos.core
  (:require [rum.core :as rum]
            [cljs-todos.actions :refer [state boundActions]]
            [cljs-todos.components :refer [app]]
            [cljs-todos.routes]
            [cljs-todos.firebase :as firebase]))

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
(add-watch state :firebase-update
  (fn [_ _ _ state]
    (firebase/save-current-user-state state)))
(add-watch firebase/user :update-listeners
  (fn [_ _ _ new-user]
    (let [uid (.-uid new-user)
          refname (str "users/" uid)]
      (.on (.ref firebase/db refname)
           "value"
           (fn [result]
             (let [js-state (.val result)
                   new-state (js->clj js-state :keywordize-keys true)]
               (reset! state new-state)
               (.log js/console "firebase state changed:" js-state new-state)))))))
(render @state)
(defn on-js-reload [] (render @state))
