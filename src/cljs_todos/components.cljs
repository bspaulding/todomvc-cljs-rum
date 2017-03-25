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
                                        showAllTodos
                                        showRemainingTodos
                                        showCompletedTodos
                                        updateNewTodoDescription
                                        addTodo]]))

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

(rum/defc filter-option [text selected onClick]
  [:li
   [:a {:class (if selected "selected")
        :on-click onClick} text]])

(rum/defc filter-options [visibility showAllTodos showRemainingTodos showCompletedTodos]
  [:ul.filters
   (filter-option "All" (= visibility "all") showAllTodos)
   (filter-option "Remaining" (= visibility "remaining") showRemainingTodos)
   (filter-option "Completed" (= visibility "completed") showCompletedTodos)])

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
   (filter-options (:visibility state) showAllTodos showRemainingTodos showCompletedTodos)
   (clear-button)])

(rum/defc app [state]
  [:div
   (header "todos" (new-todo-description state) updateNewTodoDescription addTodo)
   (main state)
   (footer state)])

