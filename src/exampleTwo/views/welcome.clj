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
         (common/layout
           [:p "Welcome to exampleTwo"]))
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

