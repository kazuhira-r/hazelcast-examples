(require '[leiningen.exec :as exec])

(exec/deps '[[com.hazelcast/hazelcast "3.0.2"]])

(ns hazelcast-topic
  (:import (com.hazelcast.config Config)
           (com.hazelcast.core Hazelcast HazelcastInstance Message MessageListener ITopic)))

(try
  ;; HazelcastInstanceを作成する
  (let [^Config config (Config.)
        ^HazelcastInstance instance (Hazelcast/newHazelcastInstance config)]
    ;; Topicを取得する
    (let [^ITopic topic (. instance getTopic "my-topic")]
      ;; TopicにMessageListenerを登録する
      (. topic addMessageListener (proxy [MessageListener] []
                                      (onMessage [^Message message]
                                                 (println (format "Received Message[%s] => %s"
                                                                  message
                                                                  (. message getMessageObject)))))))

    (. (. instance getList "counter") add "subscriber")

    ;; メッセージを受信しきるまで、擬似的に待つ
    (Thread/sleep 5000)

    ;; HazelcastInstanceをシャットダウンする
    (.. instance getLifecycleService shutdown))

  ;; 全Hazelcastのインスタンスをシャットダウンする
  (finally (Hazelcast/shutdownAll)))