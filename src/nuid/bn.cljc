(ns nuid.bn
  (:refer-clojure :exclude [str mod])
  (:require
   [clojure.string :as string]
   [nuid.bn.impl]
   [nuid.bn.proto :as proto]
   #?@(:clj
       [[clojure.alpha.spec.gen :as gen]
        [clojure.alpha.spec :as s]]
       :cljs
       [[clojure.spec.gen.alpha :as gen]
        [clojure.test.check.generators]
        [clojure.spec.alpha :as s]])))

(s/def ::string
  (s/and
   string?
   (fn [s] (re-matches #"^[\-]?[1-9][0-9]+$" s))))

(s/def ::representation
  (s/or
   ::proto/bn ::proto/bn
   ::string   ::string))

(s/def ::string<>bn
  (s/conformer
   (fn [x]
     (let [c (s/conform ::representation x)]
       (cond
         (s/invalid? c)           ::s/invalid
         (= ::string   (first c)) (proto/from (second c))
         (= ::proto/bn (first c)) (second c)
         :else                    ::s/invalid)))
   (fn [x]
     (let [c (s/conform ::representation x)]
       (cond
         (s/invalid? c)           ::s/invalid
         (= ::string   (first c)) (second c)
         (= ::proto/bn (first c)) (proto/str (second c))
         :else                    ::s/invalid)))))

(s/def ::bn
  (s/with-gen
    ::string<>bn
    (fn []
      (->>
       (gen/vector (gen/gen-for-pred nat-int?) 5 100)
       (gen/fmap (partial drop-while zero?))
       (gen/such-that not-empty)
       (gen/fmap string/join)
       (gen/fmap proto/from)
       (gen/fmap (fn [n] (if (zero? (rand-int 2)) (proto/neg n) n)))))))

(defn from
  ([x]       (proto/from x))
  ([x radix] (proto/from x radix)))

(defn add [a b] (proto/add a b))
(defn mul [a b] (proto/mul a b))
(defn lt? [a b] (proto/lt? a b))
(defn eq? [a b] (proto/eq? a b))
(defn mod [a m] (proto/mod a m))
(defn neg [a]   (proto/neg a))

(defn str
  ([a]       (proto/str a))
  ([a radix] (proto/str a radix)))
