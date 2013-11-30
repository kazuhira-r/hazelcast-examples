(require '[leiningen.exec :as exec])

(exec/deps '[[com.hazelcast/hazelcast "3.1.2"]])

(ns hazelcast-partition-service
  (:import (com.hazelcast.config Config)
           (com.hazelcast.core Hazelcast HazelcastInstance IMap Member Partition PartitionService)))

(try
  (let [^Config config (Config.)
        ^HazelcastInstance hazelcast (Hazelcast/newHazelcastInstance config)
        ^IMap map (. hazelcast getMap "map")
        ^PartitionService ps (.. hazelcast getPartitionService)]

    ;; データを表示
    (doseq [i (range 1 10)]
      (println (format "key%d = %s" i (. map get (str "key" i)))))

    ;; 適当にデータを登録
    (doseq [i (range 1 11)]
      (. map put (str "key" i) (str "value" i)))

    (doseq [i (range 1 11)]
      (let [^Partition partition (. ps getPartition (str "key" i))
            ^Member owner (.. partition getOwner)]
        (println (format "Key = %s, Owner = %s"
                         (str "key" i)
                         owner))))

    ;; HazelcastInstanceをシャットダウン
    (.. hazelcast getLifecycleService shutdown))

  ;; 全HazelcastInstanceをシャットダウン
  (finally (Hazelcast/shutdownAll)))
