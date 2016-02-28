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

  (defn save-exercise [exercise]
    "Saves an exercise"
    (let [result (mc/insert-and-return db coll (select-keys exercise [:name :category]))]
      (id-to-str result)))

  (defn update-exercise [id exercise]
    "Updates the exercise with the given id"
    (let [result (mc/update-by-id db coll (ObjectId. id) (select-keys exercise [:name :category]))]
      (println result)
      (assoc exercise :_id id)))

  (defn delete-exercise [request]
    "Deletes the exercise"
    (when-authenticated request
      (let [id (:id (:params request))
            result (mc/remove-by-id db coll (ObjectId. id))]
        {:status 200 :body {:message "Removed successfully."}}))))
