(ns exerciserapi.handler
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [ring.middleware.json :as middleware]
            [compojure.route :as route]
            [exerciserapi.exercises :as exercises]))

(defroutes app-routes
  (context "/exercises" [] (defroutes exercises-routes
    (GET "/" [] (exercises/get-exercises))
    (POST "/" request (exercises/save-exercise (:body request))) 
    (context "/:id" [id] (defroutes exercise-routes
      (GET "/" [] (exercises/get-exercise id))))))
   (route/not-found "Not Found"))

(def app
  (-> (handler/api app-routes)
      (middleware/wrap-json-body {:keywords? true})
      middleware/wrap-json-response))
