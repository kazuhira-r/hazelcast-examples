(ns hazelcast-entry-processor.core
  (:gen-class)
  (:import (com.hazelcast.config Config InMemoryFormat MapConfig)
           (com.hazelcast.core Hazelcast HazelcastInstance IMap)
           (java.util Date)
           (hazelcast-entry-processor.processor DoublingProcessor)))

(defn with-hazelcast [f]
  (try
    (let [^Config config (Config.)
          ^HazelcastInstance instance (Hazelcast/newHazelcastInstance config)]
      ;; シリアライズのコストを抑えるため、フォーマットをOBJECTにすることを推奨
      (. (. config getMapConfig "default") setInMemoryFormat (InMemoryFormat/OBJECT))
      (try
        (f instance)
        (finally (.. instance getLifecycleService shutdown))))
    (finally (Hazelcast/shutdownAll))))

;; ジョブのマスタ
(defn start-master [mode]
  (with-hazelcast
    (fn [instance]
      (let [^IMap single-map (. instance getMap "single-map")
            ^IMap all-keys-map (. instance getMap "all-keys-map")]
        ;; ===== 単一のキーに対して処理を行う =====
        (println "========== Single Key Process ==========")
        (. single-map put "key1" 1)
        (. single-map put "key2" 2)
        (. single-map put "key3" 3)
        (let [result (. single-map executeOnKey "key1" (DoublingProcessor.))]
          (println (format "single-process-result: key[%s] value[%s]" "key1" result)))

        (println "========================================")

        (println "============ All Key Process ===========")
        ;; ===== Distributed Mapの全キーに対して処理を行う =====
        ;; 指定がない場合は、Distributed Mapの中身を再登録する
        (when (not (= mode "no-refresh"))
          (doseq [i (range 1 11)]
            (. all-keys-map put (str "key" i) i)))

        ;; IMap#executeOnEntriesの引数に、EntryProcessorを与えることで分散処理
        (let [^Map result-map (. all-keys-map executeOnEntries (DoublingProcessor.))]

          ;; 処理結果を表示して確認する
          (doseq [result-entry result-map]
            (println (format "execute-result: key[%s] value[%s]"
                             (. result-entry getKey)
                             (. result-entry getValue)))))

        (println "============ In Distributed Map Entries ===================")

        ;; オリジナルのDistributed Mapの中身を確認する
        (doseq [original-entry all-keys-map]
          (println (format "original-entry: key[%s] value[%s]"
                           (. original-entry getKey)
                           (. original-entry getValue))))))))

;; ジョブ待ちのサーバ
(defn start-worker []
  (with-hazelcast
   (fn [instance]
     (println (format "[%s] Worker Started." (Date.)))
     (doseq [_ (take-while (fn [l] false)
                           (repeatedly read-line))]))))

(defn -main
  [& args]
  (case (count args)
    0 (start-worker)
    (case (first args)
      "worker" (start-worker)
      "master" (start-master (second args)))))
