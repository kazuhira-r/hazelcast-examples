(require '[leiningen.exec :as exec])

(exec/deps '[[com.hazelcast/hazelcast "3.0.2"]])

(ns hazelcast-topic
  (:import (com.hazelcast.config Config)
           (com.hazelcast.core Hazelcast HazelcastInstance Message MessageListener ITopic)))
(try
  ;; HazelcastInstanceを作成する
  (let [^Config config (Config.)
        ^HazelcastInstance instance (Hazelcast/newHazelcastInstance config)]
    ;; 他のサーバを待ち合わせ
    (let [xs (. instance getList "counter")]
      (doseq [_ (take-while
                 false?
                 (repeatedly (fn []
                               (do (println "Waiting...")
                                   (Thread/sleep 3000)
                                   (= (count xs) 2)))))]))

    ;; Topicを取得する
    (let [^ITopic topic (. instance getTopic "my-topic")]
      ;; メッセージを送信する
      (. topic publish "Hello")
      (. topic publish "Hoge")
      (. topic publish "Foo")
      (. topic publish "Bar"))

    ;; 他のサーバがメッセージを受信するまで、少し待つ
    (Thread/sleep 15000)

    ;; HazelcastInstanceをシャットダウンする
    (.. instance getLifecycleService shutdown))

  ;; 全Hazelcastのインスタンスをシャットダウンする
  (finally (Hazelcast/shutdownAll)))