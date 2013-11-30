(require '[leiningen.exec :as exec])

(exec/deps '[[com.hazelcast/hazelcast "3.0.2"]])

(ns hazelcast-multimap
  (:import (com.hazelcast.config Config)
           (com.hazelcast.core Hazelcast HazelcastInstance MultiMap)))

(try
  ;; HazelcastInstanceを作成する
  (let [^Config config (Config.)
        ^HazelcastInstance instance (Hazelcast/newHazelcastInstance config)]
    ;; MultiMapを取得する
    (let [^MultiMap customer-multimap (. instance getMultiMap "customers")]
      ;; 特定のキーに紐付く値は、Collectionとして取得できる
      (doseq [col (. customer-multimap get "NEW HORIZEN")]
        (doseq [v col] (println (str "NEW HORIZEN => " v))))

      ;; MultiMapにデータを登録する
      (. customer-multimap put "ISONOKE" {:name "Katsuo"})
      (. customer-multimap put "ISONOKE" {:name "Wakame"})
      (. customer-multimap put "ISONOKE" {:name "Tarao"}))

    ;; HazelcastInstanceをシャットダウンする
    (.. instance getLifecycleService shutdown))

  ;; 全Hazelcastのインスタンスをシャットダウンする
  (finally (Hazelcast/shutdownAll)))
