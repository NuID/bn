<p align="right"><a href="https://nuid.io"><img src="https://nuid.io/svg/logo.svg" width="20%"></a></p>

# nuid.bn

Cross-platform arbitrary precision integers.

## Requirements

[`jvm`](https://www.java.com/en/download/), [`node + npm`](https://nodejs.org/en/download/), [`clj`](https://clojure.org/guides/getting_started), [`shadow-cljs`](https://shadow-cljs.github.io/docs/UsersGuide.html#_installation)

## Clojure and ClojureScript

### tools.deps:

`{nuid/bn {:git/url "https://github.com/nuid/bn" :sha "..."}}`

### usage:

```
$ clj # or shadow-cljs node-repl
=> (require '[nuid.bn :as bn])
=> (def a (bn/from "42"))
=> (def b (bn/from "14159265358979323846264338327950288419716939937510"))
=> (def c (bn/add a b))
=> (bn/eq? a b)  ;; => false
=> (bn/lt? a b)  ;; => true
=> (bn/str c)    ;; => "14159265358979323846264338327950288419716939937552"
=> (bn/str c 16) ;; => "9b02b6aef2f4c6d5f1a5aae08bf77321e33e47710"
```

## Licensing

Apache v2.0 or MIT
