(ns nuid.bn.impl
  (:require
   [nuid.bn.proto :as proto]))

(extend-protocol proto/BNable
  (type (byte-array 0))
  (from
    ([x]   (BigInteger. 1 x))
    ([x _] (BigInteger. 1 x)))

  java.lang.String
  (from
    ([x]   (proto/from x 10))
    ([x r] (BigInteger. x r)))

  java.math.BigInteger
  (from
    ([x]   x)
    ([x _] x))

  clojure.lang.BigInt
  (from
    ([x]   (.toBigInteger x))
    ([x _] (.toBigInteger x))))

(extend-type java.math.BigInteger
  proto/BN
  (add [a b] (.add a b))
  (mul [a b] (.multiply a b))
  (lt? [a b] (< a b))
  (eq? [a b] (= a b))
  (mod [a m] (.mod a m))
  (neg [a]   (.negate a))
  (str
    ([a]   (proto/str a 10))
    ([a r] (.toString a r))))

(extend-type clojure.lang.BigInt
  proto/BN
  (add [a b] (.add a b))
  (mul [a b] (.multiply a b))
  (lt? [a b] (< a b))
  (eq? [a b] (= a b))
  (mod [a m] (.mod (.toBigInteger a) m))
  (neg [a]   (- a))
  (str
    ([a]   (proto/str a 10))
    ([a r] (.toString (.toBigInteger a) r))))
