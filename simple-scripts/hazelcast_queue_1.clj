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
      ;; Queueにデータを登録する
      (. customer-queue offer {:name "Taro" :age 22})
      (. customer-queue offer {:name "Hanako" :age 20})
      (. customer-queue offer {:name "Ken" :age 18})

      ;; 別のサーバがQueueのデータを取得するのを待つ
      (doseq [_ (take-while
                 true?
                 (repeatedly (fn []
                               (do (println "Waiting...")
                                   (Thread/sleep 3000)
                                   (empty? customer-queue)))))])

      ;; 別のサーバがシャットダウンするのを待つ
      (Thread/sleep 10000)

      ;; Queueの中身を全て取り出して表示
      (doseq [_ (range 0 (count customer-queue))]
        (println (str "queue entry => " (. customer-queue poll)))))

    ;; HazelcastInstanceをシャットダウンする
    (.. instance getLifecycleService shutdown))
  ;; 全Hazelcastのインスタンスをシャットダウンする
  (finally (Hazelcast/shutdownAll)))
