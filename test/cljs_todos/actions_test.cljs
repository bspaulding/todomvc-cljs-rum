(ns cljs-todos.actions-test
  (:require [cljs.test :as t :refer-macros [deftest testing is]]
            [cljs-todos.actions :as a]))

(deftest test-update-new-todo-description []
  (is (= {:new-todo-description "foo"}
         (a/updateNewTodoDescription {} "foo"))))
