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
    [record]
    (if (valid-record? record)
      (let  [username (:username record)
            password (:password record)
            user  (mc/find-one-as-map db coll {:username username})]
        (if (nil? user)
          {:error "User not found"} ;return nothing
          (if (hashers/check password (:password user))
            (let [id (clojure.core/str (:_id user))
                  claims {:user id
                          :exp (time/plus (time/now) (time/seconds 3600))}
                  token (jws/sign claims secret {:alg :hs512})]
              {:token token :username username})
            {:error "Wrong password provided."})))
      {:error "Validation failed" :message (validations record)})))
