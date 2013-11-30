(require '[leiningen.exec :as exec])

(exec/deps '[[com.hazelcast/hazelcast-client "3.0.2"]])

(ns hazelcast.getting-started-client
  (:import (com.hazelcast.client HazelcastClient)
           (com.hazelcast.client.config ClientConfig)
           (com.hazelcast.core HazelcastInstance IMap IQueue)))

(try
  (let [^ClientConfig clientConfig (-> (ClientConfig.)
                                       ;; デフォルトのIPアドレス／ポート
                                       (.addAddress (into-array ["127.0.0.1:5701"])))
        ^HazelcastInstance client (HazelcastClient/newHazelcastClient clientConfig)]

    ;; Mapを使う
    (let [^IMap map (.getMap client "customers")]
      (println (str "Map Size: " (count map)))
      (println (str "Customer with key 1: " (.get map 1)))
      (println (str "Customer with key 3: " (.get map 3))))

    ;; Queueを使う
    (let [^IQueue queue (.getQueue client "customers")]
      (println (str "First customer: " (.poll queue)))
      (println (str "Queue size: " (count queue))))

    ;; Clientをシャットダウンする
    (.. client getLifecycleService shutdown))

  ;; 全Hazelcastのインスタンスをシャットダウンする
  (finally (HazelcastClient/shutdownAll)))
