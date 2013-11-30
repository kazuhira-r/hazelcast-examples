(ns hazelcast-continuous-query.listener
  (:import (com.hazelcast.core EntryEvent)
           (java.util Date)))

(gen-class :name hazelcast-continuous-query.listener.MyListener
           :implements [com.hazelcast.core.EntryListener])

(defn- log [^String msg]
  (println (str \[ (.. (Thread/currentThread) getName) \ \  msg)))

(defn -entryAdded [this ^EntryEvent event]
  (log (str "added event => " event)))

(defn -entryEvicted [this ^EntryEvent event]
  (log (str "evicted event => " event)))

(defn -entryRemoved [this ^EntryEvent event]
  (log (str "removed event => " event)))

(defn -entryUpdated [this ^EntryEvent event]
  (log (str "updated event => " event)))

