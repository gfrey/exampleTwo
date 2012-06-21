(ns exampleTwo.views.welcome
  (:require [exampleTwo.models.user :as user]
            [exampleTwo.views.common :as common]
            [hiccup.form-helpers :as form]
            [noir.content.getting-started]
            [noir.response :as resp]
            [noir.session :as session])
  (:use [noir.core :only [defpage render]]
        [hiccup.core :only [html]]))

(defpage "/welcome" []
  (let [user (session/get :user)]
    (common/layout
     [:h1 (str "Welcome to exampleTwo "
              (when (not (nil? user))
                (:name user)))])))

(defpage [:get "/register"] {:keys [msg] :as input}
  (common/layout
   [:h1 "Register User"]
   [:p (when (not (nil? msg)) msg)]
   (form/form-to [:put "/register"]
                 (form/text-field "username" "UserName")
                 (form/password-field "password")
                 (form/submit-button "submit"))
   [:h2 "Known Users"]
   [:ul
    (for [user (user/get-users)]
      [:li (form/form-to [:delete (str "/user/" user)]
                         (form/label "user" user)
                         (form/submit-button "delete"))])]))

(defpage [:put "/register"] {:keys [username password]}
  (if (nil? (user/add-user username password))
    (render "/register" {:msg (format "Failed to add user %s. Already exists!" username)})
    (render "/register" {:msg (format "Added user %s" username)})))

(defpage [:delete "/user/:id"] {:keys [id]}
  (user/remove-user id)
  (render "/register" {:msg (format "Deleted user %s" id)}))

(defpage [:get "/login"] {:keys [msg username password]}
  (common/layout
   [:h1 "Login"]
   [:p (when (not (nil? msg)) msg)]
   (form/form-to [:put "/login"]
                 (form/text-field "username" username)
                 (form/password-field "password" password)
                 (form/submit-button "Login"))))

(defpage [:get "/logout"] []
  (if-let [user (session/get :user)]
    (do
      (session/remove! :user)
      (common/layout
       [:p (str "Bye " (:name user))]))
    (resp/redirect "/welcome")))

(defpage [:put "/login"] {:keys [username password] :as user}
  (if-let [user (user/valid? username password)]
    (do
      (session/put! :user user)
      (resp/redirect "/welcome"))
    (render "/login" (assoc user :msg "Failed to validate user credentials."))))


