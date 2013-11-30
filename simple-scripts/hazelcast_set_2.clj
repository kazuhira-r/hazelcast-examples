(require '[leiningen.exec :as exec])

(exec/deps '[[com.hazelcast/hazelcast "3.0.2"]])

(ns hazelcast-set
  (:import (com.hazelcast.config Config)
           (com.hazelcast.core Hazelcast HazelcastInstance)
           (java.util Set)))

(try
  ;; HazelcastInstanceを取得する
  (let [^Config config (Config.)
        ^HazelcastInstance instance (Hazelcast/newHazelcastInstance config)]
    ;; Setを取得する
    (let [^Set customer-set (. instance getSet "customers")]
      ;; Setの中身を全て表示
      (doseq [e customer-set] (println (format "Entry => %s" e)))

      ;; Setにデータを登録する
      (. customer-set add {:name "Katuo" :age 11})
      (. customer-set add {:name "Wakame" :age 7})
      (. customer-set add {:name "Tarao" :age 3}))

    ;; HazelcastInstanceをシャットダウンする
    (.. instance getLifecycleService shutdown))

  ;; 全Hazelcastのインスタンスをシャットダウンする
  (finally (Hazelcast/shutdownAll)))