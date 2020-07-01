(ns nuid.bn.impl
  (:require
   [nuid.bn.proto :as proto]
   ["bn.js" :as bn.js]
   ["buffer" :as b]))

(extend-protocol proto/BNable
  array
  (from
    ([x]   (proto/from x 10))
    ([x r] (bn.js/BN. x r)))

  js/Uint8Array
  (from
    ([x]   (proto/from x 10))
    ([x r] (bn.js/BN. x r)))

  b/Buffer
  (from
    ([x]   (proto/from x 10))
    ([x r] (bn.js/BN. x r)))

  string
  (from
    ([x]   (proto/from x 10))
    ([x r] (bn.js/BN. x r))))

(extend-type bn.js/BN
  proto/BN
  (add [a b] (.add a b))
  (mul [a b] (.mul a b))
  (lt? [a b] (.lt a b))
  (eq? [a b] (.eq a b))
  (mod [a m] (.mod a m))
  (neg [a]   (.neg a))
  (str
    ([a]   (proto/str a 10))
    ([a r] (.toString a r))))
