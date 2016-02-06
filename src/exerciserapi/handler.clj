(ns exerciserapi.handler
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [ring.middleware.json :as middleware]
            [compojure.route :as route]
            [exerciserapi.exercises :as exercises]
            [exerciserapi.authentication :as auth]
            [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]))

(require  '[buddy.auth.backends.token :refer (jws-backend)])

(def prefix "/api")
(def secret (:secret (with-open [r (clojure.java.io/reader "./config/secret.clj")]
               (read (java.io.PushbackReader. r))))) 

(def backend (jws-backend {:secret secret :options {:alg :hs512}}))

(defroutes app-routes
  (context (str prefix "/auth") [] (defroutes authentication-routes
    (POST "/login" request (auth/login-handler request))))
  (context (str prefix "/exercises") [] (defroutes exercises-routes
    (GET "/" {params :params} (exercises/get-exercises params))
    (POST "/" [] exercises/save-exercise) 
    (context "/:id" [id] (defroutes exercise-routes
      (GET "/" [] (exercises/get-exercise id))
      (PUT "/" [] exercises/update-exercise)
      (DELETE "/" [] (exercises/delete-exercise id))))))
   (route/not-found "Not Found"))

(def app
  (-> (handler/api app-routes)
      (wrap-authorization backend)
      (wrap-authentication backend)
      (middleware/wrap-json-body {:keywords? true})
      middleware/wrap-json-response))
