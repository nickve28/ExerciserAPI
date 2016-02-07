(ns exerciserapi.authentication
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [validateur.validation :refer :all]
            [exerciserapi.bson :refer :all]
            [clj-time.core :as time]
            [buddy.hashers :as hashers])
  (:import org.bson.types.ObjectId))

(require '[buddy.sign.jws :as jws])
(require '[cheshire.core :as json])

(def validations (validation-set
                   (presence-of :username)
                   (presence-of :password)))

(def valid-record? (partial valid? validations))

(def secret (:secret (with-open [r (clojure.java.io/reader "./config/secret.clj")]
               (read (java.io.PushbackReader. r)))))

(let [conn (mg/connect)
  db (mg/get-db conn "exerciser")
  coll "user"]
  (defn login-handler
    [request]
    (if (valid-record? (:body request))
      (let [data (:body request)
            username (:username data)
            password (:password data)
            user  (mc/find-one-as-map db coll {:username username})]
        (if (nil? user)
          {:status 404 :body "User not found"}
          (if (hashers/check password (:password user))
            (let [id (clojure.core/str (:_id user))
                  claims {:user id
                          :exp (time/plus (time/now) (time/seconds 3600))}
                  token (jws/sign claims secret {:alg :hs512})]
              {:status 200 :body (json/encode {:token token}) })
            {:status 400 :body "Wrong password provided."})))
      {:status 400 :body (validations (:body request))})))
