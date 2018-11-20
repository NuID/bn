(ns runner-main
  (:require
   [clojure.test :as t]
   [bn-test]))

(defn start []
  (t/run-tests
   'bn-test))

(defn stop [done]
  (done))

(defn ^:export init []
  (start))
