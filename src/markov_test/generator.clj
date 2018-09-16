(ns markov-test.generator)

(defn expand-words [coll k v]
  (into coll (repeat v k)))

(defn get-next-word [m]
  (let [expanded (reduce-kv expand-words [] (dissoc m :total))]
    (rand-nth expanded)))

(defn create-sentence [chain start sentence]
  (let [mappi (get chain start)
        next-word (get-next-word mappi)]
    (if (= :end next-word)
      sentence
      (recur chain next-word (str sentence " " next-word)))))

(defn generate-sentence [starting-words chain]
  (let [start (rand-nth (seq starting-words))]
    (create-sentence chain start start)))
