(ns guestbook.routes.home
  (:require [compojure.core :refer :all]
            [guestbook.views.layout :as layout]
            [guestbook.models.db :as db]
            [hiccup.form :refer :all]
            [noir.session :as session]
  );;require
)


(defn format-time [timestamp]
  (-> "dd/MM/yyyy"
      (java.text.SimpleDateFormat.)
      (.format timestamp)
  )
);;format-time


(defn show-guests []
  [:ul.guests
      (for [{:keys [message name timestamp]} (db/read-guests)]

          [:li
            [:div
              [:p message]
              [:p "~ " [:cite name]]
              [:time (format-time timestamp)]
              [:p ""]
            ]
          ]
      );;for
   ];;ul.guests
);;show-guests


(defn home [& [name message error]]
  (layout/common
    [:h1 "My Guestbook"]
    [:p "Welcome to my guestbook. You are " (session/get :user)]
    [:p.error error]

    (show-guests)
    [:hr]

    (form-to [:post "/"]
        [:p "Name:"]
        (text-field "name" name)
        [:p "Message:"]
        (text-area {:rows 10 :cols 40} "message" message)
        [:br]
        (submit-button "comment")
    );;form-top
  );;layout/common
);;home


(defn save-message [name message]
  (cond
      (empty? name)
      (home name message "Some dummy forgot to leave a name")

      (empty? message)
      (home name message "Don't you have anything to say?")

      :else
      (do
        (db/save-message name message)
        (println name message)
        (home)
      );;do
  );;cond
);;save-message


(defroutes home-routes
  (GET "/" [] (home))
  (POST "/" [name message] (save-message name message))
);;defroutes
