(require '[leiningen.exec :as exec])

(exec/deps '[[com.hazelcast/hazelcast "3.0.2"]])

(ns hazelcast-map
  (:import (com.hazelcast.config Config)
           (com.hazelcast.core Hazelcast HazelcastInstance)
           (java.util Map)))

(try
  ;; HazelcastInstanceを作成する
  (let [^Config config (Config.)
        ^HazelcastInstance instance (Hazelcast/newHazelcastInstance config)]
    ;; Mapを取得する
    (let [^Map customer-map (. instance getMap "customers")]
      ;; 取得したMapに登録してある情報を出力する
      (doseq [entry customer-map]
        (println (str "receive entry, " entry)))

      ;; Mapにデータを登録する
      (. customer-map put "customer-4" {:name "Katsuo" :age 11})
      (. customer-map put "customer-5" {:name "Wakame" :age 7})
      (. customer-map put "customer-1" {:name "Tarao" :age 3}))

    ;; HazelcastInstanceをシャットダウンする
    (.. instance getLifecycleService shutdown))

  ;; 全Hazelcastのインスタンスをシャットダウンする
  (finally (Hazelcast/shutdownAll)))