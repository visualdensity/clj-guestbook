(ns guestbook.routes.auth
  (:require [compojure.core :refer :all]
            [guestbook.views.layout :as layout]
            [guestbook.models.db :as db]
            [hiccup.form :refer :all]
            [noir.session :as session]
            [noir.response :refer [redirect]]
            [noir.util.crypt :as crypt]
            [noir.validation :refer [rule errors? has-value? on-error]]
  );;require
)

(defn form-item [field name text]
  (list
    (label name text)
    (field name)
    [:br]
  );;list
);;form-item


(defn register-page [& [error]]
  (layout/common
    [:h1 "Register"]
    [:p.error error]
    (form-to
      [:post "/register"]
      (form-item text-field     :id    "Username")
      (form-item password-field :pass  "Password")
      (form-item password-field :pass1 "Confirm pass")
      (submit-button "Create")
    )
  )
);;register-page


(defn login-page [& [error]]
  (layout/common
    [:h1 "Login"]
    [:p.error error]
    (form-to
      [:post "/login"]
      (form-item text-field :id "Username")
      (form-item password-field :pass "Password")
      (submit-button "Login")
    )
  )
);;login-page


(defn handle-login [id pass]
  (cond
    (empty? id)
    (login-page "Please enter username")

    (empty? pass)
    (login-page "Please enter pass")

    :else
    (let
      [user (db/read-user id)]

      (cond
        (not (crypt/compare pass (:pass user)) )
        (login-page "Invalid user pass")

        :else
        (do
          (session/put! :user id)
          (redirect "/")
        )
      );;cond
    );;let
  );;cond
);;login-page


(defn handle-register [id pass pass1]
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
      (db/save-user id (crypt/encrypt pass))
      (redirect "/login")
    )
  );;cond
);;login-page


(defroutes auth-routes

  (GET  "/login" [_] (login-page))
  (POST "/login" [id pass] (handle-login id pass))

  (GET  "/register" [_] (register-page))
  (POST "/register" [id pass pass1] (handle-register))

);;defroutes
