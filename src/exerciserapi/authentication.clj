(ns exerciserapi.authentication
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [validateur.validation :refer :all]
            [exerciserapi.bson :refer :all])
  (:import org.bson.types.ObjectId))

(require '[buddy.sign.jws :as jws])
(require '[cheshire.core :as json])

(def secret (:secret (with-open [r (clojure.java.io/reader "./config/secret.clj")]
               (read (java.io.PushbackReader. r)))))

(let [conn (mg/connect)
  db (mg/get-db conn "exerciser")
  coll "user"]

  (defn login-handler
    [request]
    (let [data (:body request)
          user  (mc/find-one-as-map db coll {:username (:username data) :password (:password data) })
          id    (clojure.core/str (:_id user))
          token (jws/sign {:user id} secret)]
      (if (nil? user)
          {:status 400 :body "Incorrect credentials"}
          {:status 200 :body (json/encode {:token token}) }))))
