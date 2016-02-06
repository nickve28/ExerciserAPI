(ns exerciserapi.authentication)

(require '[buddy.sign.jws :as jws])
(require '[cheshire.core :as json])
(def secret "foo")

(defn login-handler
  [request]
  (let [data (:params request)
        user {:username "foo" :password "bar" :id 1}
        token (jws/sign {:user (:id user)} secret)]
    {:status 200
     :body (json/encode {:token token})}))
