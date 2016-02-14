(ns exerciserapi.verification
  (:require [buddy.auth :refer [authenticated? throw-unauthorized]]))

(defmacro when-authenticated [request form]
  "Wrapper that evaluates whether a request is authorized. If authorized, it will proceed with the given forms, otherwise a 401 is given"
  `(if-not (authenticated? ~request)
    {:body { :message "You need to be authorized"} :status 401}
    ~form))
