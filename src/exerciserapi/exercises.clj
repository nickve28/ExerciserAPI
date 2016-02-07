(ns exerciserapi.exercises
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [validateur.validation :refer :all]
            [exerciserapi.bson :refer :all]
            [clj-time.core :as time]
            [buddy.auth :refer [authenticated? throw-unauthorized]])
  (:import org.bson.types.ObjectId))

(def validations
  (validation-set
    (presence-of :name)
    (presence-of :category)))

(def valid-record? (partial valid? validations))

(defn http-response 
  ([data status]
    (http-response data status nil))
  ([data status message]
    {:body data :status status :message message}))

(defmacro when-authenticated [request form]
  "Wrapper that evaluates whether a request is authorized. If authorized, it will proceed with the given forms, otherwise a 401 is given"
  (list 'if-not '(authenticated? request)
    (http-response {:message "You need to be authorized"} 401)
    form))

(let [conn (mg/connect)
  db (mg/get-db conn "exerciser")
  coll "exercises"]

  (defn get-exercises [params]
   "Retrieves all exercises, allows filter"
    (let [result (mc/find-maps db coll params)]
      (http-response (map (fn [entry]
        (id-to-str entry))
        result)
        200)))

  (defn get-exercise [id]
    "Finds exercise based on id"
    (let [result (mc/find-one-as-map db coll {:_id (ObjectId. id)})]
      (http-response (id-to-str result) 200)))
  
  (defn save-exercise [request]
    "Saves an exercise"
    (when-authenticated request
      (let [data (:body request)]
        (if (valid-record? data)
          (let [result (mc/insert-and-return db coll (select-keys data [:name :category]))]
            (http-response (id-to-str result) 201))
          (http-response {:result (validations data) } 400)))))
 
  (defn update-exercise [request]
    "Updates the exercise with the given id"
    (when-authenticated request
      (let [id (:id (:params request))
            data (:body request)]
        (if (valid-record? data)
          (let [result (mc/update-by-id db coll (ObjectId. id) (select-keys data [:name :category]))]
            (http-response {:result "Update successfull"}  200))
        (http-response {:result (validations data) } 400)))))

  (defn delete-exercise [id]
    "Deletes the exercise"
    (let [result (mc/remove-by-id db coll (ObjectId. id))] (http-response {:result "Deleted"} 200))))
