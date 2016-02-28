(ns exerciserapi.handler
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [exerciserapi.exercises :as exercises]
            [exerciserapi.workouts :as workouts]
            [exerciserapi.authentication :as auth]
            [exerciserapi.verification :refer [when-authenticated]]
            [exerciserapi.middleware.token_auth :refer [token-auth-middleware]]
            [ring.middleware.cors :refer [wrap-cors]]
            [buddy.auth :refer [authenticated? throw-unauthorized]]
            [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]))
(require '[schema.core :as s])

;todo
(s/defschema Exercise
  {:name s/Str
   :category s/Str
   :_id s/Str})

(s/defschema User
  {:username s/Str
   :token s/Str})

(defn get-exercises [params]
  (let [filtered-params (into {} (filter second params))]
    (ok (exercises/get-exercises filtered-params))))

(defn get-exercise [id]
  (let [result (exercises/get-exercise id)]
    (if (:name result)
      (ok result)
      (not-found {:message (str "Nothing found for for id" id)}))))

(defn save-exercise [name category]
  (let [result (exercises/save-exercise {:name name :category category})]
    (created result)))

(defn login [username password]
  (let [result (auth/login-handler {:username username :password password})]
    (if (:error result)
      (bad-request result)
      (ok result))))

(defn authenticated-middleware
    "Middleware used in routes that require authentication. If request is not
       authenticated a 401 not authorized response will be returned"
  [handler]
  (fn [request]
    (if (authenticated? request)
       (handler request)
       (unauthorized {:error "Not authorized"}))))

(def exercise-routes
  (context "/exercises" []
        :tags ["exercises"]
        (GET "/" []
          :summary "retrieves exercises"
          :query-params [{category :- String nil}]
          :return [Exercise]
          (get-exercises {:category category}))
        (GET "/:id" []
          :summary "retrieves exercise by id"
          :path-params [id :- String]
          :return Exercise
          (get-exercise id))
        (POST "/" []
          :summary "Saves an exercise"
          :body-params [name :- String, category :- String]
          :header-params [{Authorization :- String nil}]
          :middleware [token-auth-middleware authenticated-middleware]
          :return Exercise
          (save-exercise name category))))

(def auth-routes
  (context "/auth" []
    :tags ["auth"]
    (POST "/login" []
      :summary "Logs the user in"
      :body-params [username :- String, password :- String]
      :return User
      (login username password))))


(defapi app
  {:swagger
    {:ui "/api-docs"
     :spec "/swagger.json"
     :data {:info {:title "ExerciserApi"
                   :version "0.0.1"}
            :tags [{:name "auth"         :description "Authentication routes"}
                   {:name "exercises"    :description "Routes for the exercise model"}]}}}
    (context "/api" []
      exercise-routes
      auth-routes))

;(def app
;  (-> (handler/api app-routes)
;      (wrap-cors routes #".*")
;      (wrap-authorization backend)
;      (wrap-authentication backend)
;      (middleware/wrap-json-body {:keywords? true})
;      middleware/wrap-json-response))
