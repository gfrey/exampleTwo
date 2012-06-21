(ns exampleTwo.views.common
  (:require [noir.session :as session])
  (:use [noir.core :only [defpartial]]
        [hiccup.page-helpers :only [include-css html5]]))

(defpartial layout [& content]
            (html5
              [:head
               [:title "exampleTwo"]
               (include-css "/css/reset.css")]
              [:body
               [:nav
                [:a {:href "/register"} "register"] " "
                (if-let [user (session/get :user)]
                  [:a {:href "/logout"} "logout"]
                  [:a {:href "/login"} "login"])]
               [:div#wrapper
                content]]))
