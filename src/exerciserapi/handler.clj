(ns exerciserapi.handler
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [exerciserapi.exercises :as exercises]
            [exerciserapi.workouts :as workouts]
            [exerciserapi.authentication :as auth]
            [exerciserapi.verification :refer [when-authenticated]]
            [ring.middleware.cors :refer [wrap-cors]]
            [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]))
(require '[schema.core :as s])
(require  '[buddy.auth.backends.token :refer (jws-backend)])

(def secret (:secret (with-open [r (clojure.java.io/reader "./config/secret.clj")]
               (read (java.io.PushbackReader. r)))))

(def backend (jws-backend {:secret secret :options {:alg :hs512}}))

;todo
(s/defschema Exercise
  {:name s/Str
   :category s/Str
   :_id s/Str})

(defn get-exercises [request]
  (ok (exercises/get-exercises (:params request))))

(defn get-exercise [id]
  (ok (exercises/get-exercise id)))

(defn save-exercise [request]
  (when-authenticated request
    (exercises/save-exercise (:body request))))

(defn authenticate [username password]
  (let [result (auth/login-handler {:username username :password password})]
    (if (:error result)
      (bad-request result)
      (ok result))))

(defapi app
  (swagger-routes)
    (context "/api" []
      (context "/exercises" []
        :tags ["exercises"]
        (GET "/" []
          :summary "retrieves exercises"
          :query-params [{category :- String nil}]
          :return [Exercise]
          (get-exercises (category)))
        (GET "/:id" []
          :summary "retrieves exercise by id"
          :path-params [id :- String]
          :return Exercise
          (get-exercise id))
        (POST "/" []
          :summary "Saves an exercise"
          :body-params [name :- String, category :- String]
          :return Exercise
          save-exercise))
      (context "/auth" []
        :tags ["auth"]
        (POST "/login" []
          :summary "Logs the user in"
          :body-params [username :- String, password :- String]
          :return String
          (authenticate username password)))))


;(def app
;  (-> (handler/api app-routes)
;      (wrap-cors routes #".*")
;      (wrap-authorization backend)
;      (wrap-authentication backend)
;      (middleware/wrap-json-body {:keywords? true})
;      middleware/wrap-json-response))
