(ns anafi-flightplan-utils.share
  (:require [clojure.string :as string])
  (:gen-class))

(defn tuples [coll]
  "Given a seq return a sequence of tuples. ie [a b c d] returns [[ab] [bc] [cd] [d nil]"
  (when-let [s (seq coll)]
    (cons [(first s) (second s)] (tuples (rest s)))))

