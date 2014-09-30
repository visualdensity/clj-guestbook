(ns guestbook.handler
  (:require [compojure.core :refer [defroutes routes]]

            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [hiccup.middleware :refer [wrap-base-url]]

            [noir.session :as session]
            [ring.middleware.session.memory :refer [memory-store]]

            [compojure.handler :as handler]
            [compojure.route :as route]

            [guestbook.routes.home :refer [home-routes]]
            [guestbook.routes.auth :refer [auth-routes]]
            [guestbook.models.db :as db]
    );;require
);;guestbook.handler

(defn init []
  (println "guestbook is starting")
  (if-not (.exists (java.io.File. "./db.sq3") )
    (db/create-guestbook-table)
  )
);;init

(defn destroy []
  (println "guestbook is shutting down")
);;destroy

(defroutes app-routes
  (route/resources "/")
  (route/not-found "Not Found")
);;defroutes

(def app
  (->
      (handler/site
        (routes auth-routes
                home-routes
                app-routes
        )
      );;handler/site

      (session/wrap-noir-session
        {:store (memory-store)}
      );;session
  )
);;app
