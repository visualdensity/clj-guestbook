(ns guestbook.models.db
  (:require [clojure.java.jdbc :as sql])
  (:import java.sql.DriverManager)
)

(def db {:classname   "org.sqlite.JDBC",
         :subprotocol "sqlite",
         :subname     "db.sq3"
        }
)

(defn create-guestbook-table []
  (sql/with-connection
    db
    (sql/create-table
      :guestbook
      [:id "INTEGER PRIMARY KEY AUTOINCREMENT"]
      [:timestamp "TIMESTAMP DEFAULT CURRENT_TIMESTAMP"]
      [:name "TEXT"]
      [:message"TEXT"]
    )

    (sql/do-commands "CREATE INDEX timestamp_index ON guestbook (timestamp)")
  )
)

(defn create-user-table []
  (sql/with-connection
    db
    (sql/create-table
      :user
      [:id "INTEGER PRIMARY KEY AUTOINCREMENT"]
      [:timestamp "TIMESTAMP DEFAULT CURRENT_TIMESTAMP"]
      [:user "TEXT"]
      [:pass "TEXT"]
    )

    (sql/do-commands "CREATE INDEX user_index ON user (user)")
  )
)


(defn read-guests []
  (sql/with-connection
    db
    (sql/with-query-results res
      ["SELECT * FROM guestbook ORDER BY timestamp DESC"]
      (doall res)
    )
  )
)


(defn save-message [name message]
  (sql/with-connection
    db
    (sql/insert-values
      :guestbook
      [:name :message :timestamp]
      [name message (new java.util.Date)]
    )
  )
)

(defn save-user [user pass]
  (sql/with-connection
    db
    (sql/insert-values
      :user
      [:user :pass :timestamp]
      [user pass (new java.util.Date)]
    )
  )
)
