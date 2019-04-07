(ns nuid.bn
  (:require
   [cognitect.transit :as t]
   #?@(:cljs [["bn.js" :as bnjs]]))
  (:refer-clojure :exclude [str]))

(defrecord BN [n])

(defn add [a b]
  (->BN (.add (.-n a) (.-n b))))

(defn mul [a b]
  (->BN #?(:clj (.multiply (.-n a) (.-n b))
           :cljs (.mul (.-n a) (.-n b)))))

(defn lte? [a b]
  #?(:clj (<= (.-n a) (.-n b))
     :cljs (.lte (.-n a) (.-n b))))

(defn eq? [a b]
  #?(:clj (= (.-n a) (.-n b))
     :cljs (.eq (.-n a) (.-n b))))

(defn modulus [a m]
  (->BN (.mod (.-n a) (.-n m))))

(defn neg [a]
  (->BN #?(:clj (.negate (.-n a))
           :cljs (.neg (.-n a)))))

(defn from
  ([s] (from s 10))
  ([s radix] (->BN #?(:clj (condp = (type s)
                             java.lang.String (BigInteger. s radix)
                             (BigInteger. 1 s))
                      :cljs (bnjs/BN. s radix)))))

(defn str
  ([bn] (str bn 10))
  ([bn base] (.toString (.-n bn) base)))

(def tag "bn")

(def write-handler
  {BN (t/write-handler (constantly tag) (fn [n] (str n 16)))})

(def read-handler
  {tag (t/read-handler (fn [n] (from n 16)))})

#?(:cljs (def exports
           #js {:writeHandler write-handler
                :readHandler read-handler
                :modulus modulus
                :toString str
                :from from
                :lte lte?
                :add add
                :mul mul
                :neg neg
                :tag tag
                :eq eq?}))
