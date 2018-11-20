(ns nuid.bn
  (:require
   [cognitect.transit :as t]
   #?@(:cljs [["bn.js" :as bnjs]])))

(defrecord BN [n])

(defn add [a b]
  (->BN (.add (.-n a) (.-n b))))

(defn mul [a b]
  (->BN #?(:clj (.multiply (.-n a) (.-n b))
           :cljs (.mul (.-n a) (.-n b)))))

(defn lte [a b]
  #?(:clj (<= (.-n a) (.-n b))
     :cljs (.lte (.-n a) (.-n b))))

(defn eq [a b]
  #?(:clj (= (.-n a) (.-n b))
     :cljs (.eq (.-n a) (.-n b))))

(defn modulus [a m]
  (->BN (.mod (.-n a) (.-n m))))

(defn neg [a]
  (->BN #?(:clj (.negate (.-n a))
           :cljs (.neg (.-n a)))))

(defn str->bn
  ([s] (str->bn s 10))
  ([s radix]
   (->BN #?(:clj (condp = (type s)
                   java.lang.String (BigInteger. s radix)
                   (BigInteger. 1 s))
            :cljs (bnjs/BN. s radix)))))

(defn bn->str
  ([bn] (bn->str bn 10))
  ([bn radix] (-> bn .-n (.toString radix))))

(def tag (str ::BN))

(def write-handler
  {BN (t/write-handler
       (constantly tag)
       (fn [n] (bn->str n 16)))})

(def read-handler
  {tag (t/read-handler
        (fn [n] (str->bn n 16)))})

#?(:cljs (def exports
           #js {:write-handler write-handler
                :read-handler read-handler
                :str->bn str->bn
                :bn->str bn->str
                :modulus modulus
                :add add
                :mul mul
                :lte lte
                :neg neg
                :tag tag
                :eq eq}))
