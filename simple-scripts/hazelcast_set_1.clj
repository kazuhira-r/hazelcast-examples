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
      ;; Setにデータを登録する
      (. customer-set add {:name "Taro" :age 22})
      (. customer-set add {:name "Hanako" :age 20})
      (. customer-set add {:name "Ken" :age 18})

      ;; 別のサーバがSetにデータを追加するのを待つ
      (doseq [_ (take-while
                 false?
                 (repeatedly (fn [] (do (println "Waiting...")
                                        (Thread/sleep 3000)
                                        (= (count customer-set) 6)))))])

      ;; 別のサーバシャットダウンするのを待つ
      (Thread/sleep 5000)

      ;; Setの中身を全て表示
      (doseq [e customer-set] (println (format "Entry => %s" e))))

    ;; HazelcastInstanceをシャットダウンする
    (.. instance getLifecycleService shutdown))

  ;; 全Hazelcastのインスタンスをシャットダウンする
  (finally (Hazelcast/shutdownAll)))