(ns hazelcast-entry-processor.processor
  (import (com.hazelcast.map EntryBackupProcessor EntryProcessor)
          (java.util Map Map$Entry)))

(gen-class
 :name hazelcast-entry-processor.processor.DoublingProcessor
 :implements [com.hazelcast.map.EntryBackupProcessor
              com.hazelcast.map.EntryProcessor])

(defn -process [this ^Map$Entry entry]
  (println (format "My Key[%s] Value[%s]" (. entry getKey) (. entry getValue)))
  ;; Doubling
  (. entry setValue (* (. entry getValue) 2))
  (. entry getValue))

;; ドキュメントのサンプルと同じく、EntryBackupProcessorは自分自身
(defn -getBackupProcessor [this]
  this)

(defn -processBackup [this ^Map$Entry entry]
  (println (format "My Backup Key[%s] Value[%s]" (. entry getKey) (. entry getValue)))
  ;; バックアップも、2倍
  (. entry setValue (* (. entry getValue) 2)))
