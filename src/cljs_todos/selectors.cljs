(ns cljs-todos.selectors)

(defn new-todo-description [state]
  (:new-todo-description state))

(defn visibleTodos [state]
  (case (:visibility state)
    "all" (:todos state)
    "completed" (filter :completed (:todos state))
    "remaining" (remove :completed (:todos state))))

(defn todos-empty? [state]
	(= 0 (count (:todos state))))
