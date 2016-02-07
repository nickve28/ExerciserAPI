(ns exerciserapi.workouts
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [validateur.validation :refer :all]
            [exerciserapi.bson :refer :all]
            [buddy.auth :refer [authenticated? throw-unauthorized]])
  (:import org.bson.types.ObjectId))

(defmacro when-authenticated [request form]
  "Wrapper that evaluates whether a request is authorized. If authorized, it will proceed with the given forms, otherwise a 401 is given"
  (list 'if-not '(authenticated? request)
    {:body { :message "You need to be authorized"} :status 401}
    form))

(let [conn (mg/connect)
  db (mg/get-db conn "exerciser")
  coll "workouts"]

  (defn get-workouts [request]
    "Retrieves all workouts"
    (let [result (mc/find-maps db coll (:params request))]
      {:status 200 :body (map id-to-str result)})))

