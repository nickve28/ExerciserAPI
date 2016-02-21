(ns exerciserapi.workouts
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [validateur.validation :refer :all]
            [exerciserapi.bson :refer :all]
            [exerciserapi.verification :refer [when-authenticated]]
            [exerciserapi.exercises :refer [get-exercises]]
            [buddy.auth :refer [authenticated? throw-unauthorized]])
  (:import org.bson.types.ObjectId))

(def exercises (future (map :name (:body (get-exercises {})))))

(def validations (validation-set
                   (presence-of :name)
                   (presence-of :category)
                   (presence-of :date)
                   (presence-of :exercises)
                   (validate-with-predicate :exercises
                      (fn [workout]
                        (let [performed-exercises (:exercises workout)
                              names (map :name performed-exercises)]
                          (every? #(contains? (set @exercises) %) names))))))

(def valid-record? (partial valid? validations))

(let [conn (mg/connect)
  db (mg/get-db conn "exerciser")
  workout-col "workouts"
  exercises-col "exercises"]

  (defn get-workouts [request]
    "Retrieves all workouts"
    (when-authenticated request
      (let [result (mc/find-maps db workout-col (:params request))]
        {:status 200 :body (map id-to-str result)})))

  (defn get-workout [request]
    "Retrieves a single workout"
    (when-authenticated request
      (let [id (:id (:params request))
            result (mc/find-one-as-map db workout-col {:_id (ObjectId. id)})]
       {:status 200 :body (id-to-str result)})))

  (defn save-workout [request]
    "Saves a workout and its exercises"
    (when-authenticated request
      (let [workout (:body request)]
        (if-not (valid-record? workout)
          {:status 400 :body (validations workout) }
          (let [result (mc/insert-and-return db workout-col workout)]
            {:status 201 :body (id-to-str result) }))))))
