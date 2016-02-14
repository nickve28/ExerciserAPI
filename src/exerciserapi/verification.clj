(ns exerciserapi.verification)

(defmacro when-authenticated [pred form]
  "Wrapper that evaluates whether a request is authorized. If authorized, it will proceed with the given forms, otherwise a 401 is given"
  `(if-not ~pred
    {:body { :message "You need to be authorized"} :status 401}
    ~form))
