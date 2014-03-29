;; Execution Command: $ lein exec hazelcast-getting-started.clj

(require '[leiningen.exec :as exec])

(exec/deps '[[com.hazelcast/hazelcast "3.2"]])

(import '(com.hazelcast.config Config)
        '(com.hazelcast.core Hazelcast))

(let [cfg (Config.)
      instance (Hazelcast/newHazelcastInstance cfg)]

  (let [map-customers (. instance getMap "customers")]
    (doseq [[i name] '([1 "Joe"] [2 "Ali"] [3 "Avi"])]
      (. map-customers put i name))
    (println (str "Customer with key 1: " (. map-customers get 1)))
    (println (str "Map Size: " (. map-customers size))))

  (let [queue-customers (. instance getQueue "customers")]
    (doseq [name ["Tom" "Mary" "Jane"]]
      (. queue-customers offer name))
    (println (str "First customer: " (. queue-customers poll)))
    (println (str "Second customer: " (. queue-customers peek)))
    (println (str "Queue size: " (. queue-customers size))))

  (.. instance getLifecycleService shutdown))