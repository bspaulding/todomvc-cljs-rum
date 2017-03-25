(ns cljs-todos.actions
  (:require [cljs-todos.selectors :refer [new-todo-description visibleTodos]]))

;; singleton atom

(defonce state
  (atom
   {:todos [{:description "Get Bread" :completed false}
            {:description "Call Phil" :completed false}
            {:description "i am done" :completed true}]
    :visibility "all"
    :new-todo-description ""}))

;; actions

(defn updateNewTodoDescription [text]
  (swap! state assoc :new-todo-description text))
(defn updateTodoDescription [i text]
  (swap! state assoc-in [:todos i :description] text))

(defn addTodo []
  (swap! state update-in [:todos]
         (fn [todos]
           (conj todos {:description (new-todo-description @state)})))
  (swap! state assoc :new-todo-description ""))

(defn toggleTodoCompleted [i]
  (swap! state update-in [:todos i]
         (fn [todo]
           {:description (:description todo)
            :completed (not (:completed todo))})))

(defn clearCompletedTodos []
  (swap! state update-in [:todos]
         (fn [todos]
           (into [] (remove :completed todos)))))

(defn showAllTodos []
  (swap! state assoc :visibility "all"))

(defn showRemainingTodos []
  (swap! state assoc :visibility "remaining"))

(defn showCompletedTodos []
  (swap! state assoc :visibility "completed"))

(defn mark-completed [completed]
  (fn [todo]
    {:description (:description todo)
     :completed completed}))

(defn toggleAllCompleted []
  (let [all-completed (every? :completed (:todos @state))]
    (swap! state update-in [:todos]
           (fn [todos]
             (into [] (map (mark-completed (not all-completed)) todos))))))

(defn removeTodo [i]
  (swap! state update-in [:todos]
         (fn [todos]
           (into [] (concat (take i todos) (drop (+ 1 i) todos))))))

(defn toggleEditing [i]
  (swap! state update-in [:todos i]
         (fn [todo]
           {:description (:description todo)
            :completed (:completed todo)
            :editing (not (:editing todo))})))
