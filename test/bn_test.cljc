(ns bn-test
  (:require
   [clojure.test :as t]
   [clojure.test.check :as tc]
   [clojure.test.check.generators :as gen]
   [clojure.test.check.properties :as prop :include-macros true]
   [nuid.bn :as bn]
   #?@(:clj  [[clojure.test.check.clojure-test :as ct :refer [defspec]]]
       :cljs [[clojure.test.check.clojure-test :as ct :refer-macros [defspec]]])))

(def gen-bn-str
  (gen/let [negative? gen/boolean]
    (->>
     (gen/vector gen/nat)
     (gen/such-that not-empty)
     (gen/such-that (fn [n] (if (> (count n) 1) (not (zero? (first n))) true)))
     (gen/fmap (fn [n] (if (and negative? (not (zero? (first n)))) (cons "-" n) n)))
     (gen/fmap clojure.string/join))))

(def gen-bn (gen/fmap bn/from gen-bn-str))

(ct/defspec string-roundtrip 5000
  (prop/for-all
   [i gen-bn-str]
   (= (bn/str (bn/from i)) i)))

(ct/defspec eq 5000
  (prop/for-all
   [a gen-bn]
   (bn/eq? a a)))

(ct/defspec add-commutative 5000
  (prop/for-all
   [a gen-bn
    b gen-bn]
   (bn/eq?
    (bn/add a b)
    (bn/add b a))))

(ct/defspec add-neg 5000
  (prop/for-all
   [a gen-bn
    b gen-bn]
   (let [a+b   (bn/add a b)
         a+b-b (bn/add a+b (bn/neg b))
         a+b-a (bn/add a+b (bn/neg a))]
     (and
      (bn/eq? a+b-a b)
      (bn/eq? a+b-b a)))))
