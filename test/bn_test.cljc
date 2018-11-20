(ns bn-test
  (:require
   #?@(:clj [[clojure.test.check.clojure-test :as ct :refer [defspec]]]
       :cljs [[clojure.test.check.clojure-test :as ct :refer-macros [defspec]]])
   [clojure.test.check.properties :as prop :include-macros true]
   [clojure.test.check.generators :as gen]
   [clojure.test.check :as tc]
   [clojure.test :as t]
   [nuid.bn :as bn]))

(def gen-bn-str
  (gen/let [negative? gen/boolean]
    (->> (gen/vector gen/nat)
         (gen/such-that not-empty)
         (gen/such-that #(if (> (count %) 1) (not (zero? (first %))) true))
         (gen/fmap #(if (and negative? (not (zero? (first %)))) (cons "-" %) %))
         (gen/fmap clojure.string/join))))

(def gen-bn
  (gen/fmap bn/str->bn gen-bn-str))

(ct/defspec string-roundtrip 5000
  (prop/for-all [i gen-bn-str]
                (= (bn/bn->str (bn/str->bn i)) i)))

(ct/defspec eq 5000
  (prop/for-all [a gen-bn]
                (bn/eq a a)))

(ct/defspec add-commutative 5000
  (prop/for-all
   [a gen-bn
    b gen-bn]
   (bn/eq (bn/add a b)
          (bn/add b a))))

(ct/defspec add-neg 5000
  (prop/for-all
   [a gen-bn
    b gen-bn]
   (let [a+b (bn/add a b)
         a+b-b (bn/add a+b (bn/neg b))
         a+b-a (bn/add a+b (bn/neg a))]
     (and (bn/eq a+b-a b)
          (bn/eq a+b-b a)))))
