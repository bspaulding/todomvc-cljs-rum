(ns cljs-todos.firebase)
  ;; (:require ["firebase/firebase-browser" :refer [initializeApp]]))

(def config
  #js{:apiKey "AIzaSyAEyDvek1smKZSqiJnY7Rc8c8ZpH1uriUA"
      :authDomain "todomvc-cljs-rum.firebaseapp.com"
      :databaseURL "https://todomvc-cljs-rum.firebaseio.com"
      :projectId "todomvc-cljs-rum"
      :storageBucket ""
      :messagingSenderId "800280210038"})

(defonce app (.initializeApp js/firebase config))
(defonce db (.database js/firebase))
(defonce github-provider
  (new js/firebase.auth.GithubAuthProvider))

(defn sign-in-with-github []
  (.then
    (.signInWithPopup (.auth app) github-provider)
    (fn [result]
      (.log js/console (.-user result)))))

(defonce user (atom nil))
(add-watch user :user-change-log
  (fn [_ _ _ _] (.log js/console "user changed")))
(add-watch user :update-listeners
  (fn [_ _ _ new-user]
    (let [uid (.-uid new-user)
          refname (str "users/" uid)]
      (.on (.ref db refname)
           "value"
           (fn [result]
             (let [js-state (.val result)
                   state (js->clj js-state :keywordize-keys true)]
              (.log js/console "firebase state changed:" js-state state)))))))

(defn serialize [state]
  (let [transformed-todos (reduce (fn [m v] (assoc m (count m) v)) {} (:todos state))]
    (assoc state :todos transformed-todos)))

(defn save-user-state [db user state]
  (let [uid (.-uid user)
        refname (str "users/" uid)
        js-state (clj->js (serialize state))]
    (.log js/console "saving state to firebase: " state js-state)
    (.set (.ref db refname) js-state)))

(defn save-current-user-state [state]
  (if @user
    (save-user-state db @user state)))

(defonce auth-state-changed
  (.onAuthStateChanged
    (.auth app)
    (fn [new-user]
      (if-not new-user
        (sign-in-with-github)
        (reset! user new-user)))))
