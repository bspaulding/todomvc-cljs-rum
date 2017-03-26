(ns cljs-todos.routes
	(:require [secretary.core :as secretary]
						[goog.events :as events]
						[goog.history.EventType :as EventType]
						[cljs-todos.actions :refer [showAllTodos showRemainingTodos showCompletedTodos]])
	(:require-macros [secretary.core :refer [defroute]])
	(:import goog.History))

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
