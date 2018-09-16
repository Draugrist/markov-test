(ns markov-test.loader)

(def valid-line #"\d{2}+:\d{2}+ (<[@| ][a-zA-Z^]+>| \* [a-zA-Z^]+) (.*)")

(defn process-file-by-lines
  "Process file reading it line-by-line"
  ([file]
   (process-file-by-lines file identity))
  ([file process-fn]
   (process-file-by-lines file process-fn println))
  ([file process-fn output-fn]
   (with-open [rdr (clojure.java.io/reader file :encoding "ISO-8859-1")]
     (doseq [line (line-seq rdr)]
       (output-fn
         (process-fn line))))))

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

(defn process-words [starting-words chain words]
  (do
    (swap! starting-words conj (first words))
    (swap! chain process-pairs (make-pairs words))))

(defn build-chain [filename]
  (let [starting-words (atom #{})
        chain (atom {})]
    (do
      (process-file-by-lines filename line->words #(process-words starting-words chain %))
      [@starting-words @chain])))

