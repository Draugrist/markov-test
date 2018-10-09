(ns markov-test.loader
  (:require [clojure.java.io :as io]))

(def valid-line #"\d{2}+:\d{2}+ (<[@| ][a-zA-Z^]+>| \* [a-zA-Z^]+) (.*)")

(defn process-file-by-lines-transduce
  [file xf f]
  (with-open [rdr (io/reader file :encoding "ISO-8859-1")]
    (transduce xf f (line-seq rdr))))

(defn parse-line [s]
  (let [[_ _ res] (re-matches valid-line s)]
    res))

(defn parse-words [s]
  (if s (clojure.string/split s #"\s+")))

(def line->words
  (comp parse-words parse-line))

(defn increase-word [m [word1 word2]]
  (let [prev-map (get m word1 {word2 0 :total 0})
        new-map (assoc prev-map word2 (inc (get prev-map word2 0))
                                :total (inc (get prev-map :total)))]
    (assoc m word1 new-map)))

(defn make-pairs [words]
  (partition 2 1 [:end] words))

(defn process-pairs [m pairs]
  (if (empty? pairs)
    m
    (recur (increase-word m (first pairs)) (rest pairs))))

(defn chain-builder-transduce
  ([]
   [#{} {}])
  ([x]
   x)
  ([[starting-words chain] words]
   [(conj starting-words (first words)) (process-pairs chain (make-pairs words))]))

(defn build-chain [filename]
  (let [xf (comp (map line->words) (remove nil?))]
    (process-file-by-lines-transduce filename xf chain-builder-transduce)))