(ns exerciserapi.exercises
  (:require [monger.core :as mg]
            [monger.collection :as mc])
  (:import org.bson.types.ObjectId))

(defn http-response 
  ([data status]
   (http-response data status nil))
  ([data status message]
    {:body data :status status :message message})
) 

(let [conn (mg/connect)
      db   (mg/get-db conn "exerciser")
      coll "exercises"]

  (defn bson-to-str [entry k]
    "Provides a workaround for chesire not supporting BSON, making it a string instead"
    (assoc entry k (clojure.core/str (k entry)) ))

  (defn id-to-str [entry]
   "Shorthand for id conversion"
   (bson-to-str entry :_id))

  (defn get-exercises [params]
   "Retrieves all exercises, allows filter"
    (let [result (mc/find-maps db coll params)]
      (http-response (map (fn [entry]
         (id-to-str entry)) 
         result
      ) 200)))

  (defn get-exercise [id]
    "Finds exercise based on id"
    (let [result (mc/find-one-as-map db coll {:_id (ObjectId. id)})]
      (http-response (id-to-str result) 200)))
  
  (defn save-exercise [data]
    "Saves an exercise"
    (let [result (mc/insert-and-return db coll (select-keys data [:name :category]))]
      (http-response (id-to-str result) 201)))
 
  (defn update-exercise [id data]
    "Updates the exercise with the given id"
    (let [result (mc/update-by-id db coll (ObjectId. id) (select-keys data [:name :category]))]
      (http-response {:result "Update successfull"}  200)))

  (defn delete-exercise [id]
    "Deletes the exercise"
    (let [result (mc/remove-by-id db coll (ObjectId. id))] (http-response {:result "Deleted"} 200)))
)
