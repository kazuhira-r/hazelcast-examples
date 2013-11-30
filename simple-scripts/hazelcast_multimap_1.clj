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
      ;; MultiMapにデータを登録する
      (. customer-multimap put "NEW HORIZEN" {:name "Ken"})
      (. customer-multimap put "NEW HORIZEN" {:name "Kumi"})
      (. customer-multimap put "NEW HORIZEN" {:name "Mike"})

      ;; この場合、要素数は3となるらしい
      (assert (= (. customer-multimap size) 3))

      (doseq [entry (. customer-multimap entrySet)]
        (println (str (. entry getKey) " => " (. entry getValue))))

      ;; 別のサーバがMultiMapにデータを登録するのを待つ
      (doseq [_ (take-while
                 false?
                 (repeatedly (fn []
                               (do (println "Waiting...")
                                   (Thread/sleep 3000)
                                   (> (. customer-multimap size) 3)))))])

      ;; 別のサーバがシャットダウンするのを待つ
      (Thread/sleep 5000)

      (assert (= (. customer-multimap size) 6))

      ;; MultiMapに登録されたデータを全て出力する
      (doseq [entry (. customer-multimap entrySet)]
        (println (str (. entry getKey) " => " (. entry getValue)))))

    ;; HazelcastInstanceをシャットダウンする
    (.. instance getLifecycleService shutdown))

  ;; 全Hazelcastのインスタンスをシャットダウンする
  (finally (Hazelcast/shutdownAll)))
