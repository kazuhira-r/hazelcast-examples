(require '[leiningen.exec :as exec])

(exec/deps '[[com.hazelcast/hazelcast "3.0.2"]])

(ns hazelcast.getting-started-server
  (:import (com.hazelcast.config Config)
           (com.hazelcast.core Hazelcast HazelcastInstance)
           (java.util Map Queue)))

(try
  (let [^Config config (Config.)
        ^HazelcastInstance instance (Hazelcast/newHazelcastInstance config)]

    ;; Mapとして使う
    (let [^Map map-customers (.getMap instance "customers")]
      (doto map-customers
        (.put 1 "Joe")
        (.put 2 "Ali")
        (.put 3 "Avi"))

      (println (str "Customer with key 1: " (.get map-customers 1)))
      (println (str "Map size: " (count map-customers))))

    ;; Queueとして使う
    (let [^Queue queue-customers (.getQueue instance "customers")]
      (doto queue-customers
        (.offer "Tom")
        (.offer "Mary")
        (.offer "Jane"))

      (println (str "First customer: " (.poll queue-customers)))
      (println (str "Second customer: " (.peek queue-customers)))
      (println (str "Queue size: " (count queue-customers))))

    (loop []
      (println "Waiting...")
      (Thread/sleep 3000)
      (recur))

    ;; HazelcastInstanceをシャットダウンする
    (.. instance getLifecycleService shutdown))

  ;; 全Hazelcastのインスタンスをシャットダウンする
  (finally (Hazelcast/shutdownAll)))
