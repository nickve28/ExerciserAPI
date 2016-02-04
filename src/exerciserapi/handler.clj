(ns exerciserapi.handler
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [ring.middleware.json :as middleware]
            [compojure.route :as route]
            [exerciserapi.exercises :as exercises]))
(def prefix "/api")

(defroutes app-routes
  (context (str prefix "/exercises") [] (defroutes exercises-routes
    (GET "/" {params :params} (exercises/get-exercises params))
    (POST "/" request (exercises/save-exercise (:body request))) 
    (context "/:id" [id] (defroutes exercise-routes
      (GET "/" [] (exercises/get-exercise id))
      (PUT "/" {params :params body :body} (exercises/update-exercise (:id params) body))
      (DELETE "/" [id] (exercises/delete-exercise id))))))
   (route/not-found "Not Found"))

(def app
  (-> (handler/api app-routes)
      (middleware/wrap-json-body {:keywords? true})
      middleware/wrap-json-response))
