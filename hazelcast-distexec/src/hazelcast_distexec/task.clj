(ns hazelcast-distexec.task
  (:import (com.hazelcast.core Hazelcast HazelcastInstance IMap)
           (java.io Serializable)
           (java.util.concurrent Callable)))

(gen-class :name hazelcast-distexec.task.SumTask
           :implements [java.util.concurrent.Callable java.io.Serializable]
           :state mapName
           :constructors {[String] []}
           :init init)

(defn -init [name]
  [[] name])

(defn -call ^Integer [this]
  (let [^HazelcastInstance hazelcast (first (Hazelcast/getAllHazelcastInstances))
        ^IMap dist-map (. hazelcast getMap (.. this mapName))
        ^Set keys (.. dist-map localKeySet)]
    (println (format "My Local Keys => %s" keys))
    (reduce #(+ (. dist-map get %2) %1) 0 keys)))
