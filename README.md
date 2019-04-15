# nuid.bn

Cross-platform arbitrary precision integers.

## Requirements

[`jvm`](https://www.java.com/en/download/), [`node + npm`](https://nodejs.org/en/download/), [`clj`](https://clojure.org/guides/getting_started), [`shadow-cljs`](https://shadow-cljs.github.io/docs/UsersGuide.html#_installation)

## From Clojure and ClojureScript

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

## From JavaScript

Until [`goog.closure`](https://github.com/google/closure-compiler/issues/3167) and the JS ecosystem generally supports native [BigInts](https://developers.google.com/web/updates/2018/05/bigint), this library is a thin wrapper over [`bn.js`](https://github.com/indutny/bn.js/)

### node:

```
$ shadow-cljs release node
$ node
> var BN = require('./target/node/nuid_bn');
> var a = BN.from("42");
> var c = BN.mul(a, BN.from("10"));
> BN.toString(c);
> BN.toString(c, 16);
```

### browser:

```
$ shadow-cljs release browser
## go use ./target/browser/nuid_bn.js in a browser script
```

## From Java

This library is a thin wrapper around Java's native `BigInteger`. To call `nuid.bn` from Java or other JVM languages, use one of the recommended interop strategies ([var/IFn](https://clojure.org/reference/java_interop#_calling_clojure_from_java) or [uberjar/aot](https://push-language.hampshire.edu/t/calling-clojure-code-from-java/865)). Doing so may require modifications or additions to the API for convenience.

## From CLR

[Coming soon.](https://github.com/clojure/clojure-clr/blob/master/Clojure/Clojure/Lib/BigInteger.cs)

## Notes

The purpose of `nuid.bn` and sibling `nuid` libraries (e.g. [`nuid.elliptic`](https://github.com/nuid/elliptic)) is to abstract over platform-specific differences and provide a common interface to fundamental dependencies. This allows us to express dependent logic (e.g. [`nuid.zk`](https://github.com/nuid/zk)) once in pure Clojure(Script), and use it from each of the host platforms (Java, JavaScript, CLR). This is particularly useful for generating and verifying proofs across service boundaries.

## Licensing

Apache v2.0 or MIT

## Contributing

Install [`git-hooks`](https://github.com/icefox/git-hooks) and fire away. Make sure not to get bitten by [`externs`](https://clojurescript.org/guides/externs) if modifying `npm` dependencies.

### formatting:

```
$ clojure -A:cljfmt            # check
$ clojure -A:cljfmt:cljfmt/fix # fix
```

### dependencies:

```
## check
$ npm outdated 
$ clojure -A:depot

## update
$ npm upgrade -s
$ clojure -A:depot:depot/update
```
