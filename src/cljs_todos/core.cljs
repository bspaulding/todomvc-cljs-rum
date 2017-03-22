(ns cljs-todos.core
  (:require [rum.core :as rum]))

(enable-console-print!)

(defonce state
  (atom
   {:todos [{:description "Get Bread" :completed false}
            {:description "Call Phil" :completed false}
            {:description "i am done" :completed true}]
    :visibility "all"
    :new-todo/description ""}))

;; selectors
(defn new-todo-description [state]
  (:new-todo/description state))
(defn visibleTodos [state]
  (case (:visibility state)
    "all" (:todos state)
    "completed" (filter :completed (:todos state))
    "remaining" (remove :completed (:todos state))))

;; actions
(defn updateNewTodoDescription [text]
  (swap! state assoc :new-todo/description text))
(defn updateTodoDescription [i text]
  (swap! state assoc-in [:todos i :description] text))

(defn addTodo []
  (swap! state update-in [:todos]
         (fn [todos]
           (conj todos {:description (new-todo-description @state)})))
  (swap! state assoc :new-todo/description ""))

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
     :checked (every? :completed (:todos state))}]
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

(defn render
  ([key ref previousState state] (render state))
  ([state]
   (print state)
   (rum/mount
    (app state)
    (. js/document (getElementById "app")))))

(add-watch state :rerender render)
(render @state)

(defn on-js-reload []
  (render @state))
