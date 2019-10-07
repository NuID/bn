(ns nuid.bn
  (:refer-clojure :exclude [str mod])
  (:require
   [nuid.transit :as transit]
   [cognitect.transit :as t]
   #?@(:clj
       [[clojure.spec-alpha2.gen :as gen]
        [clojure.spec-alpha2 :as s]]
       :cljs
       [[clojure.spec.gen.alpha :as gen]
        [clojure.test.check.generators]
        [clojure.spec.alpha :as s]
        ["bn.js" :as bn.js]
        ["buffer" :as b]])))

(defprotocol BNable
  (from [x] [x r]))

(defprotocol BN
  (add [a b])
  (mul [a b])
  (lt? [a b])
  (eq? [a b])
  (mod [a m])
  (neg [a])
  (str [a] [a r]))

(s/def ::external
  (s/and string?
         (fn [s] (re-matches #"^[\-]?[1-9][0-9]+$" s))))

(s/def ::internal
  (fn [n] (satisfies? BN n)))

(s/def ::representation
  (s/or ::external ::external
        ::internal ::internal))

(s/def ::bn
  (s/with-gen
    (s/conformer
     (fn [x]
       (let [c (s/conform ::representation x)]
         (cond
           (s/invalid? c)           ::s/invalid
           (= ::external (first c)) (from (second c))
           (= ::internal (first c)) (second c)
           :else                    ::s/invalid)))
     (fn [x]
       (let [c (s/conform ::representation x)]
         (cond
           (s/invalid? c)           ::s/invalid
           (= ::external (first c)) (second c)
           (= ::internal (first c)) (str (second c))
           :else                    ::s/invalid))))
    (fn []
      (->> (gen/vector (gen/gen-for-pred nat-int?) 5 100)
           (gen/fmap (partial drop-while zero?))
           (gen/such-that not-empty)
           (gen/fmap clojure.string/join)
           (gen/fmap from)
           (gen/fmap (fn [n] (if (zero? (rand-int 2)) (neg n) n)))))))

#?(:clj
   (extend-protocol BNable
     (type (byte-array 0))
     (from
       ([x] (BigInteger. 1 x))
       ([x _] (BigInteger. 1 x)))

     java.lang.String
     (from
       ([x] (from x 10))
       ([x r] (BigInteger. x r)))

     java.math.BigInteger
     (from
       ([x] x)
       ([x _] x))

     clojure.lang.BigInt
     (from
       ([x] (.toBigInteger x))
       ([x _] (.toBigInteger x)))))

#?(:clj
   (extend-type java.math.BigInteger
     BN
     (add [a b] (.add a b))
     (mul [a b] (.multiply a b))
     (lt? [a b] (< a b))
     (eq? [a b] (= a b))
     (mod [a m] (.mod a m))
     (neg [a] (.negate a))
     (str
       ([a] (str a 10))
       ([a r] (.toString a r)))))

#?(:clj
   (extend-type clojure.lang.BigInt
     BN
     (add [a b] (.add a b))
     (mul [a b] (.multiply a b))
     (lt? [a b] (< a b))
     (eq? [a b] (= a b))
     (mod [a m] (.mod (.toBigInteger a) m))
     (neg [a] (- a))
     (str
       ([a] (str a 10))
       ([a r] (.toString (.toBigInteger a) r)))))

#?(:cljs
   (extend-protocol BNable
     array
     (from
       ([x] (from x 10))
       ([x r] (bn.js/BN. x r)))

     js/Uint8Array
     (from
       ([x] (from x 10))
       ([x r] (bn.js/BN. x r)))

     b/Buffer
     (from
       ([x] (from x 10))
       ([x r] (bn.js/BN. x r)))

     string
     (from
       ([x] (from x 10))
       ([x r] (bn.js/BN. x r)))))

#?(:cljs
   (extend-type bn.js/BN
     BN
     (add [a b] (.add a b))
     (mul [a b] (.mul a b))
     (lt? [a b] (.lt a b))
     (eq? [a b] (.eq a b))
     (mod [a m] (.mod a m))
     (neg [a] (.neg a))
     (str
       ([a] (str a 10))
       ([a r] (.toString a r)))))

(def transit-tag "n")

(def transit-read-handler
  {transit-tag
   (t/read-handler
    (partial s/conform ::bn))})

(def transit-write-handler
  (let [cls #?(:clj java.math.BigInteger :cljs bn.js/BN)]
    {cls (t/write-handler
          (constantly transit-tag)
          (partial s/unform ::bn))}))

#?(:cljs (def exports #js {}))
