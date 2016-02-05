(ns exerciserapi.bson
  (:import org.bson.types.ObjectId))

  (defn bson-to-str [entry k]
    "Provides a workaround for chesire not supporting BSON, making it a string instead"
    (assoc entry k (clojure.core/str (k entry)) ))

  (defn id-to-str [entry]
    "Shorthand for id conversion"
    (bson-to-str entry :_id))


