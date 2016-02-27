(defproject exerciserapi "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [compojure "1.4.0"]
                 [ring/ring-defaults "0.1.5"]
                 [metosin/compojure-api "1.0.0"]
                 [jumblerg/ring.middleware.cors "1.0.1"]
                 [com.novemberain/monger "3.0.2"]
                 [com.novemberain/validateur "2.5.0"]
                 [buddy/buddy-auth "0.9.0"]
                 [buddy/buddy-hashers "0.11.0"]]
  :plugins [[lein-ring "0.9.7"]]
  :ring {:handler exerciserapi.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.0"]]}})
