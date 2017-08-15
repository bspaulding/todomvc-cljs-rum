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

(defn updateNewTodoDescription [state text]
  (assoc state :new-todo-description text))
(defn updateTodoDescription [state i text]
  (assoc-in state [:todos i :description] text))

(defn addTodo [state]
	(-> state
		(assoc :new-todo-description "")
		(update-in [:todos]
					 (fn [todos]
						 (conj todos {:description (new-todo-description state)})))))

(defn toggleTodoCompleted [state i]
  (update-in state [:todos i]
         (fn [todo]
           {:description (:description todo)
            :completed (not (:completed todo))})))

(defn clearCompletedTodos [state]
  (update-in state [:todos]
         (fn [todos]
           (into [] (remove :completed todos)))))

(defn showAllTodos [state]
  (assoc state :visibility "all"))

(defn showRemainingTodos [state]
  (assoc state :visibility "remaining"))

(defn showCompletedTodos [state]
  (assoc state :visibility "completed"))

(defn mark-completed [completed]
  (fn [todo]
    {:description (:description todo)
     :completed completed}))

(defn toggleAllCompleted [state]
  (let [all-completed (every? :completed (:todos state))]
    (update-in state [:todos]
           (fn [todos]
             (into [] (map (mark-completed (not all-completed)) todos))))))

(defn removeTodo [state i]
  (update-in state [:todos]
         (fn [todos]
           (into [] (concat (take i todos) (drop (+ 1 i) todos))))))

(defn toggleEditing [state i]
  (update-in state [:todos i]
         (fn [todo]
           {:description (:description todo)
            :completed (:completed todo)
            :editing (not (:editing todo))})))

;; binding stuff


(def actions
	{:toggle-todo-completed toggleTodoCompleted
	 :toggle-editing toggleEditing
	 :remove-todo removeTodo
	 :update-todo-description updateTodoDescription
	 :clear-completed-todos clearCompletedTodos
	 :toggle-all-completed toggleAllCompleted
	 :update-new-todo-description updateNewTodoDescription
	 :add-todo addTodo
	 :show-all-todos showAllTodos
	 :show-remaining-todos showRemainingTodos
	 :show-completed-todos showCompletedTodos})

(defn bindActions [atom actions]
  (into {}
				(map (fn [[k f]]
							 {k (fn [& args]
										(swap! atom (fn [state] (apply f state args))))})
						 actions)))

(def boundActions (bindActions state actions))

