(ns guestbook.routes.auth
  (:require [compojure.core :refer :all]
            [guestbook.views.layout :as layout]
            [guestbook.models.db :as db]
            [hiccup.form :refer :all]
            [noir.session :as session]
            [noir.response :refer [redirect]]
  );;require
)

(defn form-item [field name text]
  (list
    (label name text)
    (field name)
    [:br]
  );;list
);;form-item

(defn register-page []
  (layout/common
    (form-to
      [:post "/register"]
      [:h1 "Register"]
      (form-item text-field     :id    "Username")
      (form-item password-field :pass  "Password")
      (form-item password-field :pass1 "Confirm pass")
      (submit-button "Create")
    )
  )
);;register-page


(defn login-page []
);;login-page

(defn handle-login []
);;login-page

(defn handle-register []
);;login-page

(defroutes auth-routes
  (GET  "/register" [_] (register-page))
  (POST "/register" [id pass pass1]
    (cond
      (empty? id)
      (register-page "Please enter a username")

      (empty? pass)
      (register-page "Please enter a password")

      (empty? pass1)
      (register-page "Please confirm password")

      (not= pass pass1)
      (register-page "Make sure password same same")

      :else
      (do
        (db/save-user id pass)
        (session/put! :user id)
        (redirect "/")
      )
    );;cond
  );;POST

);;defroutes
