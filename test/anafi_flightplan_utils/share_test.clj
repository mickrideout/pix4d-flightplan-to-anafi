(ns anafi-flightplan-utils.share-test
  (:require [clojure.test :refer :all]
            [anafi-flightplan-utils.share :refer :all]))

(deftest tuples-test
  (testing "Tuples"
    (is (= '([:a :b] [:c :d] [:e nil]) (tuples [:a :b :c :d :e])))))