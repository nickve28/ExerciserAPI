(ns exerciserapi.middleware.token_auth
    (:require [buddy.auth.middleware :refer [wrap-authentication]]))
(require  '[buddy.auth.backends.token :refer (jws-backend)])

(def secret (:secret (with-open [r (clojure.java.io/reader "./config/secret.clj")]
               (read (java.io.PushbackReader. r)))))

(def backend (jws-backend {:secret secret :options {:alg :hs512}}))


(defn token-auth-middleware
  "Add token authentication to the service as middleware"
  [handler]
    (wrap-authentication handler backend))
