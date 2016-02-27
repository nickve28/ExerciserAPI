(ns exerciserapi.exercises
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [validateur.validation :refer :all]
            [exerciserapi.bson :refer :all]
            [clj-time.core :as time]
            [buddy.auth :refer [authenticated? throw-unauthorized]]
            [exerciserapi.verification :refer [when-authenticated]])
  (:import org.bson.types.ObjectId))

(def validations
  (validation-set
    (presence-of :name)
    (presence-of :category)))

(def valid-record? (partial valid? validations))

(let [conn (mg/connect)
  db (mg/get-db conn "exerciser")
  coll "exercises"]

  (defn get-exercises [params]
   "Retrieves all exercises, allows filter"
    (let [result (mc/find-maps db coll params)]
      (map id-to-str result)))

  (defn get-exercise [id]
    "Finds exercise based on id"
    (let [result (mc/find-one-as-map db coll {:_id (ObjectId. id)})]
      (id-to-str result)))

  (defn save-exercise [request]
    "Saves an exercise"
    (when-authenticated request
      (let [data (:body request)]
        (if (valid-record? data)
          (let [result (mc/insert-and-return db coll (select-keys data [:name :category]))]
            {:status 200 :body (id-to-str result)})
          {:status 400 :body (validations data)}))))

  (defn update-exercise [request]
    "Updates the exercise with the given id"
    (when-authenticated request
      (let [id (:id (:params request))
            data (:body request)]
        (if (valid-record? data)
          (let [result (mc/update-by-id db coll (ObjectId. id) (select-keys data [:name :category]))]
            {:status 200 :body (assoc data :id id)})
        {:status 400 :body (validations data)}))))

  (defn delete-exercise [request]
    "Deletes the exercise"
    (when-authenticated request
      (let [id (:id (:params request))
            result (mc/remove-by-id db coll (ObjectId. id))]
        {:status 200 :body {:message "Removed successfully."}}))))
