(require '[leiningen.exec :as exec])

(exec/deps '[[com.hazelcast/hazelcast "3.0.2"]])

(ns hazelcast-list
  (:import (com.hazelcast.config Config)
           (com.hazelcast.core Hazelcast HazelcastInstance)
           (java.text SimpleDateFormat)
           (java.util Date List)))

(try
  ;; HazelcastInstanceを取得する
  (let [^Config config (Config.)
        ^HazelcastInstance instance (Hazelcast/newHazelcastInstance config)]
    ;; Listを取得する
    (let [^List datetimes (. instance getList "datetimes")]
      ;; Listにデータを登録する
      (. datetimes add (Date.))
      (. datetimes add (Date.))
      (. datetimes add (Date.))

      ;; 別のサーバがListにデータを追加するのを待つ
      (doseq [_ (take-while
                 false?
                 (repeatedly (fn [] (do (println "Waiting...")
                                        (Thread/sleep 3000)
                                        (= (count datetimes) 6)))))])

      ;; 別のサーバシャットダウンするのを待つ
      (Thread/sleep 5000)

      ;; Listの中身を全て表示
      (doseq [d datetimes]
        (println (format "Entry => %s"
                         (. (SimpleDateFormat. "yyyy/MM/dd HH:mm:ss.SSS") format d)))))

    ;; HazelcastInstanceをシャットダウンする
    (.. instance getLifecycleService shutdown))

  ;; 全Hazelcastのインスタンスをシャットダウンする
  (finally (Hazelcast/shutdownAll)))