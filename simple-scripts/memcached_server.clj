(require '[leiningen.exec :as exec])

(exec/deps '[[com.hazelcast/hazelcast "3.1"]])

(ns hazelcast-memcached
  (:import (com.hazelcast.config Config NetworkConfig)
           (com.hazelcast.core Hazelcast HazelcastInstance)))

(try
  (let [^Config config (. (Config.)
                          setNetworkConfig
                          (. (NetworkConfig.) setPort 11211))
        ^HazelcastInstance hazelcast (Hazelcast/newHazelcastInstance config)]
    (println "Server Startup.")
    ;; Enter待ち
    (read-line)

    ;; HazelcastInstanceをシャットダウン
    (.. hazelcast getLifecycleService shutdown))

  ;; 全Hazelcastインスタンスをシャットダウン
  (finally (Hazelcast/shutdownAll)))