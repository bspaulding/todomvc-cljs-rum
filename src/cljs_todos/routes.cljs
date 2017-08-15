(ns cljs-todos.routes
	(:require [secretary.core :as secretary]
						[goog.events :as events]
						[goog.history.EventType :as EventType]
						[cljs-todos.actions :refer [boundActions]])
	(:require-macros [secretary.core :refer [defroute]])
	(:import goog.History))

(def showAllTodos (:show-all-todos boundActions))
(def showRemainingTodos (:show-remaining-todos boundActions))
(def showCompletedTodos (:show-completed-todos boundActions))

(defroute all "/" []
	(showAllTodos))

(defroute remaining "/remaining" []
	(showRemainingTodos))

(defroute completed "/completed" []
	(showCompletedTodos))

;; Quick and dirty history configuration.
(let [h (History.)]
  (goog.events/listen h EventType/NAVIGATE
											(fn [event]
												(js/console.log "navigate event")
												(secretary/dispatch! (.-token event))))
  (doto h
    (.setEnabled true)))

(secretary/dispatch! "/")
