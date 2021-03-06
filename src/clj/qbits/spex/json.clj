(ns qbits.spex.json
  "Validators/Conformers for JSON input data - uses cheshire as type
  convertion model for now. But this should be open via the ICodec
  protocol.
  todo: tests!"
  (:require
   [qbits.spex :as x]
   [clojure.spec :as s]
   [clojure.test.check.generators :as gen])
  (:import (java.util.Base64$Decoder)))

(defprotocol ICodec
  (integer-like [x])
  (float-like [x])
  (double-like [x])
  (long-like [x])
  (short-like [x])
  (biginteger-like [x])
  (bigint-like [x])
  (string-like [x])
  (keyword-like [x])
  (symbol-like [x])
  (set-like [x])
  (date-like [x])
  (uuid-like [x])
  (byte-like [x])
  (binary-like [x])
  (bool-like [x]))

(extend-protocol ICodec
  Number
  (string-like [x] (str x))

  Long
  (long-like [x] x)
  (integer-like [x] (int x))
  (float-like [x] (float x))
  (double-like [x] (double x))
  (short-like [x] (short x))
  (string-like [x] (str x))
  (date-like [x] (java.util.Date. x))

  Integer
  (integer-like [x] x)

  Float
  (float-like [x] x)

  Short
  (short-like [x] x)

  Double
  (double-like [x] x)
  (string-like [x] (str x))

  BigInteger
  (biginteger-like [x] x)

  clojure.lang.BigInt
  (bigint-like [x] x)

  String
  (string-like [x] x)
  (integer-like [x] (x/try-or-invalid (Integer/parseInt x)))
  (long-like [x] (x/try-or-invalid (Long/parseLong x)))
  (double-like [x] (x/try-or-invalid (Double/parseDouble x)))
  (short-like [x] (x/try-or-invalid (Short/parseShort x)))
  (bigint-like [x] (x/try-or-invalid (-> x BigInteger. clojure.lang.BigInt/fromBigInteger)))
  (biginteger-like [x] (x/try-or-invalid (BigInteger. x)))
  (float-like [x] (x/try-or-invalid (Float/parseFloat x)))
  (keyword-like [x] (keyword x))
  (symbol-like [x] (symbol x))
  (uuid-like [x] (x/try-or-invalid (java.util.UUID/fromString x)))
  (byte-like [x] (-> (java.util.Base64/getDecoder) (.decode x)))
  (binary-like [x] x)
  (bool-like [x] (x/try-or-invalid (Boolean/parseBoolean x)))

  Boolean
  (bool-like [x] x)

  clojure.lang.Keyword
  (keyword-like [x] x)

  clojure.lang.Symbol
  (symbol-like [x] x)

  java.util.UUID
  (uuid-like [x] x)

  java.util.Date
  (date-like [x] x)

  clojure.lang.IPersistentCollection
  (set-like [x] (set x))

  Object
  (integer-like [x] :clojure.spec/invalid)
  (float-like [x] :clojure.spec/invalid)
  (double-like [x] :clojure.spec/invalid)
  (long-like [x] :clojure.spec/invalid)
  (short-like [x] :clojure.spec/invalid)
  (biginteger-like [x] :clojure.spec/invalid)
  (bigint-like [x] :clojure.spec/invalid)
  (string-like [x] :clojure.spec/invalid)
  (set-like [x] :clojure.spec/invalid)
  (keyword-like [x] :clojure.spec/invalid)
  (symbol-like [x] :clojure.spec/invalid)
  (date-like [x] :clojure.spec/invalid)
  (uuid-like [x] :clojure.spec/invalid)
  (byte-like [x] :clojure.spec/invalid)
  (binary-like [x] :clojure.spec/invalid)
  (bool-like [x] :clojure.spec/invalid)

  nil
  (integer-like [x] :clojure.spec/invalid)
  (float-like [x] :clojure.spec/invalid)
  (double-like [x] :clojure.spec/invalid)
  (long-like [x] :clojure.spec/invalid)
  (short-like [x] :clojure.spec/invalid)
  (biginteger-like [x] :clojure.spec/invalid)
  (bigint-like [x] :clojure.spec/invalid)
  (string-like [x] :clojure.spec/invalid)
  (set-like [x] :clojure.spec/invalid)
  (keyword-like [x] :clojure.spec/invalid)
  (symbol-like [x] :clojure.spec/invalid)
  (date-like [x] :clojure.spec/invalid)
  (uuid-like [x] :clojure.spec/invalid)
  (byte-like [x] :clojure.spec/invalid)
  (binary-like [x] :clojure.spec/invalid)
  (bool-like [x] :clojure.spec/invalid))

(def nat-str-gen (gen/one-of [gen/nat (gen/fmap str gen/nat)]))

(s/def ::string
  (s/spec (s/conformer string-like)
          :gen (constantly (gen/one-of [gen/string
                                        gen/int
                                        gen/double]))))
(s/def ::integer
  (s/spec (s/conformer integer-like)
          :gen (constantly (gen/one-of [nat-str-gen gen/int]))))

(s/def ::float
  (s/spec (s/conformer float-like)
          :gen (constantly nat-str-gen)))

(s/def ::long
  (s/spec (s/conformer long-like)
          :gen (constantly nat-str-gen)))

(s/def ::double
  (s/spec (s/conformer double-like)
          :gen (constantly (gen/one-of [nat-str-gen gen/double]))))

(s/def ::short
  (s/spec (s/conformer short-like)
          :gen (constantly nat-str-gen)))

(s/def ::biginteger
  (s/spec (s/conformer biginteger-like)
          :gen (constantly nat-str-gen)))

(s/def ::bigint
  (s/conformer (s/spec bigint-like
                       :gen (constantly nat-str-gen))))

(s/def ::set
  (s/spec (s/conformer set-like)
          :gen (constantly (gen/one-of [(gen/vector gen/any)
                                        (gen/list gen/any)
                                        (gen/set gen/any)
                                        (gen/map gen/any gen/any)]))))

(s/def ::keyword
  (s/spec (s/conformer keyword-like)
          :gen (constantly (gen/one-of [gen/string gen/keyword]))))

(s/def ::symbol
  (s/spec (s/conformer symbol-like)
          :gen (constantly (gen/one-of [gen/string gen/symbol]))))

(s/def ::date
  (s/spec (s/conformer date-like)
          :gen (constantly (gen/one-of [gen/pos-int (gen/fmap #(java.util.Date %)
                                                              gen/pos-int)]))))

(s/def ::uuid
  (s/spec (s/conformer uuid-like)
          :gen (constantly (gen/one-of [(gen/fmap str gen/uuid) gen/uuid]))))

(s/def ::bool (s/spec (s/conformer bool-like)
                      :gen (constantly (gen/one-of [gen/string gen/boolean]))))


;; (s/exercise ::date)


;; (s/def ::n ::integer)
;; (s/def ::s ::string)
;; (s/def ::l ::set)
;; (s/def ::d (s/keys :opt-un [::n ::s ::l]))

;; (s/exercise ::string)
;; (s/valid? ::integer a)

;; (take 3 (s/exercise ::uuid))


;; (s/conform ::d {:l []})
;; (s/conform (::set) #{})
