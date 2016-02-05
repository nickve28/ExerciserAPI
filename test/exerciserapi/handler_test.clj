(ns exerciserapi.handler-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [exerciserapi.handler :refer :all]))

;Todo: use binding to override DB calls (mock), and test routes

(deftest test-app
 (testing "not-found route"
    (let [response (app (mock/request :get "/invalid"))]
      (is (= (:status response) 404)))))
