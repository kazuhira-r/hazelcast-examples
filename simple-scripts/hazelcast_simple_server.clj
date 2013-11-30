(require '[leiningen.exec :as exec])

(exec/deps '[[com.hazelcast/hazelcast "3.1.2"]])

(ns hazelcast-simple-server
  (:import (com.hazelcast.config Config)
           (com.hazelcast.core Hazelcast HazelcastInstance)))

(try
  ;; HazelcastInstanceの作成
  ;;(let [^Config config (Config.)
  (let [^Config config (Config.)
        ^HazelcastInstance hazelcast (Hazelcast/newHazelcastInstance config)]
    (. (. config getMapConfig "default") setBackupCount 0 )

    ;; 浮いてるだけのサーバ
    (println "Start Simple Server.")
    (read-line)

    ;; HazelcastInstanceをシャットダウン
    (.. hazelcast getLifecycleService shutdown))

  ;; 全HazelcastInstanceをシャットダウン
  (finally (Hazelcast/shutdownAll)))
