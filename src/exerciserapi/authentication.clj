(ns exerciserapi.authentication)

(require '[buddy.sign.jws :as jws])
(require '[cheshire.core :as json])

(def secret (:secret (with-open [r (clojure.java.io/reader "./config/secret.clj")]
               (read (java.io.PushbackReader. r))))) 

(defn login-handler
  [request]
  (let [data (:params request)
        user {:username "foo" :password "bar" :id 1}
        token (jws/sign {:user (:id user)} secret)]
    {:status 200
     :body (json/encode {:token token})}))
