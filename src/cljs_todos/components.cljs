(ns cljs-todos.components
  (:require [rum.core :as rum]
            [cljs-todos.selectors :refer [new-todo-description visibleTodos]]
            [cljs-todos.actions :refer [toggleTodoCompleted
                                        toggleEditing
                                        removeTodo
                                        toggleEditing
                                        updateTodoDescription
                                        clearCompletedTodos
                                        toggleAllCompleted
                                        updateNewTodoDescription
                                        addTodo]]
						[secretary.core :as secretary]))

(rum/defc input [description onChange onAdd]
  [:div
   [:input.new-todo
    {:auto-focus true
     :type "text"
     :placeholder "What needs to be done?"
     :value description
     :on-key-up (fn [event]
                  (if (= 13 (.-keyCode event)) (onAdd)))
     :on-input (fn [event]
                 (onChange (.. event -target -value)))}]])

(rum/defc header [text description updateNewTodoDescription addTodo]
  [:header.header
   [:h1 text]
   (input description updateNewTodoDescription addTodo)])

(rum/defc todo-item < {:key-fn (fn [i] i)} [index todo]
  [:li {:class (if (:completed todo) "completed" (if (:editing todo) "editing"))}
   [:div.view
    [:input.toggle {:type "checkbox" :checked (:completed todo)
                    :on-click (fn [] (toggleTodoCompleted index))}]
    [:label {:on-double-click (fn [] (toggleEditing index))} (:description todo)]
    [:button.destroy {:on-click (fn [e] (.preventDefault e) (.stopPropagation e) (removeTodo index))}]]
   [:input.edit {:value (:description todo)
                 :on-key-up
                 (fn [event]
                   (if (= 13 (.-keyCode event))
                     (toggleEditing index)))
                 :on-input
                 (fn [event] (updateTodoDescription index (.. event -target -value)))}]])

(rum/defc todos-list [todos]
  [:ul.todo-list
   (map-indexed todo-item todos)])

(rum/defc clear-button []
  [:button.clear-completed {:on-click clearCompletedTodos} "Clear Completed"])

(rum/defc link [attrs href children]
	[:a (merge attrs {:href href
										:on-click (fn [e]
																(. e preventDefault)
																(js/history.pushState {} "todos" href)
																(secretary/dispatch! href))})
	 children])

(rum/defc filter-option [text selected href]
  [:li (link {:class (if selected "selected")} href text)])

(rum/defc filter-options [visibility]
  [:ul.filters
   (filter-option "All" (= visibility "all") "/")
   (filter-option "Remaining" (= visibility "remaining") "/remaining")
   (filter-option "Completed" (= visibility "completed") "/completed")])

(rum/defc main [state]
  [:section.main
   [:input.toggle-all
    {:type "checkbox"
     :checked (and (not (empty? (:todos state))) (every? :completed (:todos state)))}]
   [:label
    {:for "toggle-all"
     :on-click toggleAllCompleted}
    "Mark all as complete"]
   (todos-list (visibleTodos state))])

(rum/defc footer [state]
  [:footer.footer
   [:span.todo-count
    [:strong (count (remove :completed (:todos state)))]
    " "
    (if (= 1 (count (remove :completed (:todos state)))) "item" "items")
    " left"]
   (filter-options (:visibility state))
   (clear-button)])

(rum/defc app [state]
  [:div
   (header "todos" (new-todo-description state) updateNewTodoDescription addTodo)
   (main state)
   (footer state)])

