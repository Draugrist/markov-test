(ns markov-test.generator)

(defn expand-words [coll k v]
  (into coll (repeat v k)))

(defn get-next-word [m]
  (let [expanded (reduce-kv expand-words [] (dissoc m :total))]
    (rand-nth expanded)))

(defn create-sentence [chain start]
  (loop [word start
         sentence start]
    (let [next-word (get-next-word (get chain word))]
      (if (= :end next-word)
        sentence
        (recur next-word (str sentence " " next-word)))))
  )

(defn generate-sentence [starting-words chain]
  (let [start (rand-nth (seq starting-words))]
    (create-sentence chain start)))
