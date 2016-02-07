(ns exerciserapi.workouts
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [validateur.validation :refer :all]
            [exerciserapi.bson :refer :all]
            [exerciserapi.verification :refer [when-authenticated]]
  (:import org.bson.types.ObjectId))

(let [conn (mg/connect)
  db (mg/get-db conn "exerciser")
  coll "workouts"]

  (defn get-workouts [request]
    "Retrieves all workouts"
    (when-authenticated request
      (let [result (mc/find-maps db coll (:params request))]
        {:status 200 :body (map id-to-str result)})))

