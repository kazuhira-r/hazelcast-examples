(require '[leiningen.exec :as exec])

(exec/deps '[[com.hazelcast/hazelcast "3.1"]])

(ns hazelcast-distexec
  (:import (com.hazelcast.config Config)
           (com.hazelcast.core ExecutionCallback Hazelcast HazelcastInstance IExecutorService Member)
           (java.util.concurrent Callable Future)))

(try
  ;; HazelcastInstanceの作成
  (let [^Config config (Config.)
        ^HazelcastInstance hazelcast (Hazelcast/newHazelcastInstance config)]
    ;; Workerは浮いてるだけ
    (println "Start Worker.")
    (read-line)

    ;; HazelcastInstanceをシャットダウン
    (.. hazelcast getLifecycleService shutdown))

  ;; 全HazelcastInstanceをシャットダウン
  (finally (Hazelcast/shutdownAll)))