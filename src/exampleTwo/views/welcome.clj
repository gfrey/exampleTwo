(ns exampleTwo.views.welcome
  (:require [exampleTwo.models.user :as user]
            [exampleTwo.views.common :as common]
            [hiccup.form-helpers :as form]
            [noir.content.getting-started]
            [noir.response :as resp]
            [noir.session :as session])
  (:use [noir.core :only [defpage]]
        [hiccup.core :only [html]]))

(defpage "/welcome" []
  (let [user (session/get :user)]
    (common/layout
     [:h1 (str "Welcome to exampleTwo "
              (when (not (nil? user))
                (:name user)))])))

(defpage [:get "/register"] []
  (common/layout
   [:h1 "Register User"]
   (form/form-to [:put "/register"]
                 (form/text-field "username" "UserName")
                 (form/password-field "password")
                 (form/submit-button "submit"))
   [:ul
    (for [user (user/get-users)]
      [:li user])]))

(defpage [:put "/register"] {:keys [username password]}
  (user/add-user username password)
  (resp/redirect "/register"))

(defpage [:get "/login"] []
  (common/layout
   [:h1 "Login"]
   (form/form-to [:put "/login"]
                 (form/text-field "username")
                 (form/password-field "password")
                 (form/submit-button "Login"))))

(defpage [:get "/logout"] []
  (if-let [user (session/get :user)]
    (do
      (session/remove! :user)
      (common/layout
       [:p (str "Bye " (:name user))]))
    (resp/redirect "/welcome")))

(defpage [:put "/login"] {:keys [username password]}
  (when-let [user (user/valid? username password)]
    (session/put! :user user))
  (resp/redirect "/welcome"))

