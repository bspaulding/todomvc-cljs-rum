(ns cljs-todos.components
  (:require [rum.core :as rum]
            [cljs-todos.selectors :refer [new-todo-description visibleTodos]]
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

(rum/defc todo-item < {:key-fn (fn [i] i)}
	[index todo actions]
	(let [{:keys [toggle-todo-completed
								toggle-editing
								remove-todo
								update-todo-description]} actions]
		[:li {:class (if (:completed todo) "completed" (if (:editing todo) "editing"))}
		 [:div.view
			[:input.toggle {:type "checkbox" :checked (:completed todo)
											:on-click (fn [] (toggle-todo-completed index))}]
			[:label {:on-double-click (fn [] (toggle-editing index))} (:description todo)]
			[:button.destroy {:on-click (fn [e] (.preventDefault e) (.stopPropagation e) (remove-todo index))}]]
		 [:input.edit {:value (:description todo)
									 :on-key-up
									 (fn [event]
										 (if (= 13 (.-keyCode event))
											 (toggle-editing index)))
									 :on-input
									 (fn [event] (update-todo-description index (.. event -target -value)))}]]))

(rum/defc todos-list [todos actions]
  [:ul.todo-list
   (map-indexed #(todo-item %1 %2 actions) todos)])

(rum/defc clear-button [clearCompletedTodos]
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

(rum/defc main [state actions]
  [:section.main
   [:input.toggle-all
    {:type "checkbox"
     :checked (and (not (empty? (:todos state))) (every? :completed (:todos state)))}]
   [:label
    {:for "toggle-all"
     :on-click (:toggle-all-completed actions)}
    "Mark all as complete"]
   (todos-list (visibleTodos state) actions)])

(rum/defc footer [state clear-completed-todos]
  [:footer.footer
   [:span.todo-count
    [:strong (count (remove :completed (:todos state)))]
    " "
    (if (= 1 (count (remove :completed (:todos state)))) "item" "items")
    " left"]
   (filter-options (:visibility state))
   (clear-button clear-completed-todos)])

(rum/defc app [state actions]
  [:div
   (header "todos" (new-todo-description state) (:update-new-todo-description actions) (:add-todo actions))
   (main state actions)
   (footer state (:clear-completed-todos actions))])

