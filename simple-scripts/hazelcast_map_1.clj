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
    ;; Mapを取得
    (let [^Map customer-map (. instance getMap "customers")]
      (. customer-map put "customer-1" {:name "Taro" :age 22})
      (. customer-map put "customer-2" {:name "Hanako" :age 20})
      (. customer-map put "customer-3" {:name "Ken" :age 18})

      ;; 別のサーバがデータを登録するのを待つ
      (doseq [_ (take-while
                 false?
                 (repeatedly (fn []
                               (do (println "Waiting...")
                                   (Thread/sleep 3000)
                                   (contains? customer-map "customer-4")))))])

      ;; 別のサーバがシャットダウンするのを待つ
      (Thread/sleep 5000)

      ;; 現在のMapの中身を表示
      (doseq [entry customer-map]
        (println (str "receive entry, " entry))))

    ;; HazelcastInstanceをシャットダウンする
    (.. instance getLifecycleService shutdown))

  ;; 全Hazelcastのインスタンスをシャットダウンする
  (finally (Hazelcast/shutdownAll)))