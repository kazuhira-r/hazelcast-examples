(require '[leiningen.exec :as exec])

(exec/deps '[[com.hazelcast/hazelcast "3.0.2"]])

(ns hazelcast-queue
  (:import (com.hazelcast.config Config)
           (com.hazelcast.core Hazelcast HazelcastInstance)
           (java.util Queue)))

(try
  ;; HazelcastInstanceを作成する
  (let [^Config config (Config.)
        ^HazelcastInstance instance (Hazelcast/newHazelcastInstance config)]
    ;; Queueを取得する
    (let [^Queue customer-queue (. instance getQueue "customers")]
      ;; Queueの中身を全て取り出して表示
      (doseq [_ (range 0 (count customer-queue))]
        (println (str "queue entry => " (. customer-queue poll))))

      (println (count customer-queue))

      ;; Queueにデータを登録する
      (. customer-queue offer {:name "Katsuo" :age 11})
      (. customer-queue offer {:name "Wakame" :age 7})
      (. customer-queue offer {:name "Tarao" :age 3}))

    ;; HazelcastInstanceをシャットダウンする
    (.. instance getLifecycleService shutdown))
  ;; 全Hazelcastのインスタンスをシャットダウンする
  (finally (Hazelcast/shutdownAll)))
