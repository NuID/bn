(ns nuid.bn
  (:require
   [nuid.transit :as transit]
   [cognitect.transit :as t]
   #?@(:cljs [["bn.js" :as bn.js]]))
  (:refer-clojure :exclude [str mod]))

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

#?(:clj
   (extend-protocol BNable
     (type (byte-array 0))
     (from
       ([x] (BigInteger. 1 x))
       ([x _] (BigInteger. 1 x)))

     java.lang.String
     (from
       ([x] (from x 10))
       ([x r] (BigInteger. x r)))))

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
       ([a r] (.toString a r)))

     transit/TransitWritable
     (rep [x] (str x 16))))

#?(:cljs
   (extend-protocol BNable
     js/Buffer
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
       ([a r] (.toString a r)))

     transit/TransitWritable
     (rep [x] (str x 16))))

(def tag "bn")

(def read-handler
  {tag (t/read-handler #(from % 16))})

(def write-handler
  (let [c #?(:clj java.math.BigInteger :cljs bn.js/BN)]
    {c (t/write-handler (constantly tag) #(transit/rep %))}))

#?(:cljs
   (def exports
     #js {:writeHandler write-handler
          :readHandler read-handler
          :toString str
          :from from
          :mod mod
          :add add
          :mul mul
          :neg neg
          :tag tag
          :lt lt?
          :eq eq?}))
