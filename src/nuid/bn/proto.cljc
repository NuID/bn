(ns nuid.bn.proto
  (:refer-clojure :exclude [str mod])
  (:require
   #?@(:clj  [[clojure.alpha.spec :as s]]
       :cljs [[clojure.spec.alpha :as s]])))

(defprotocol BNable
  (from
    [x]
    [x radix]))

(defprotocol BN
  (add [a b])
  (mul [a b])
  (lt? [a b])
  (eq? [a b])
  (mod [a m])
  (neg [a])
  (str
    [a]
    [a radix]))

(s/def ::bn
  (fn [n] (satisfies? BN n)))
