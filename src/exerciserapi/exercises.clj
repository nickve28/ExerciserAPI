(ns exerciserapi.exercises
  (:require [monger.core :as mg]
            [monger.collection :as mc])
  (:import org.bson.types.ObjectId))

(let [conn (mg/connect)
      db   (mg/get-db conn "exerciser")
      coll "exercises"]

  (defn id-to-str [entry]
    "Provides a workaround for chesire not supporting BSON, making it a string instead" 
    (assoc entry :_id (clojure.core/str (:_id entry)) ))
  
  (defn get-exercises []
   "Retrieves all exercises" 
    (let [result (mc/find-maps db coll)]
      {:body (map (fn [entry]
         (id-to-str entry)) 
         result
      )}))

  (defn get-exercise [id]
    "Finds exercise based on id"
    (let [result (mc/find-one-as-map db coll {:_id (ObjectId. id)})]
      {:body (id-to-str result) }))
  )
