(ns markov-test.core
  (:require [markov-test.loader :as loader]
            [markov-test.generator :as generator])
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [[starting-words chain] (loader/build-chain (first args))]
    (dotimes [_ 10]
      (println (generator/generate-sentence starting-words chain)))))

;(def markov (loader/build-chain "irssi-log-format.txt"))
;(dotimes [_ 10] (println (generator/generate-sentence (first markov) (second markov))))