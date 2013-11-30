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
    ;; ExecutorServiceの取得
    (let [^IExecutorService executorService (. hazelcast getExecutorService "default")]

      ;; submitの場合は、クラスタ上のどこかで実行される
      (let [^Callable task #(println "hoge")
            ^Future f (. executorService submitToMembers task (.. hazelcast getCluster getMembers))]
        (println (format "submit result => %s" (. f get)))))

    ;; HazelcastInstanceをシャットダウン
    (.. hazelcast getLifecycleService shutdown))

  ;; 全HazelcastInstanceをシャットダウン
  (finally (Hazelcast/shutdownAll)))