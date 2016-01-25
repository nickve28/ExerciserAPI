(ns exerciserapi.exercises
  (:require [monger.core :as mg]
            [monger.collection :as mc])
  (:import org.bson.types.ObjectId))

(let [conn (mg/connect)
      db   (mg/get-db conn "exerciser")
      coll "exercises"]

  (defn bson-to-str [entry k]
    "Provides a workaround for chesire not supporting BSON, making it a string instead"
    (assoc entry k (clojure.core/str (k entry)) ))

  (defn id-to-str [entry]
   "Shorthand for id conversion"
   (bson-to-str entry :_id))

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
  
  (defn save-exercise [data]
    "Saves an exercise"
    (let [result (mc/insert-and-return db coll (select-keys data [:name :category]))]
      {:body (id-to-str result)}))
)
