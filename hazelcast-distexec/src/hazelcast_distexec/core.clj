(ns hazelcast-distexec.core
  (:gen-class)
  (:import (com.hazelcast.config Config)
           (com.hazelcast.core Hazelcast HazelcastInstance IExecutorService IMap Member)
           (java.util Date Map)
           (java.util.concurrent Future)
           (hazelcast-distexec.task SumTask)))

(defn- with-hazelcast [f]
  (try
    ;; HazelcastInstanceの作成
    (let [^Config config (Config.)
          ^HazelcastInstance hazelcast (Hazelcast/newHazelcastInstance config)]
      (f hazelcast)
      ;; HazelcastInstanceをシャットダウン
      (.. hazelcast getLifecycleService shutdown))

    ;; 全HazelcastInstanceをシャットダウン
    (finally (Hazelcast/shutdownAll))))

(defn- start-master []
  (with-hazelcast
    (fn [hazelcast]
      ;; ExecutorServiceの取得
      (let [^String map-name "dist-map"
            ^IMap dist-map (. hazelcast getMap map-name)
            ^IExecutorService executorService (. hazelcast getExecutorService "default")]
        ;; データの初期登録
        (doseq [i (range 1 11)]
          (. dist-map put i i))

        ;; submitで、クラスタ内のどこかのメンバーで実行される
        (let [^Callable task (SumTask. map-name)
              ^Future f (. executorService submit task)]
          (println "=============== submit[START] ===============")
          (println (format "value = %s" (.. f get)))
          (println "=============== submit[END] ==============="))

        (println)

        ;; submitToMemberで、指定したMemberで実行される
        (let [^Callable task (SumTask. map-name)
              ^Member member (first (.. hazelcast getCluster getMembers))
              ^Future f (. executorService submitToMember task member)]
          (println "=============== submitToMember[START] ===============")
          (println (format "%s, value = %s" member (.. f get)))
          (println "=============== submitToMember[END] ==============="))

        (println)

        ;; submitToKeyOwnerで、指定したキーの所有者Memberで実行される
        (let [^Callable task (SumTask. map-name)
              key 5
              ^Future f (. executorService submitToKeyOwner task key)]
          (println "=============== submitToKeyOwner[START] ===============")
          (println (format "spec key = %s, value = %s" key (.. f get)))
          (println "=============== submitToKeyOwner[END] ==============="))

        (println)

        ;; submitToMembersで、Setで指定したMemberで実行される
        (let [^Callable task (SumTask. map-name)
              ^Map future-map (. executorService submitToMembers task (.. hazelcast getCluster getMembers))]
          (println "=============== submitToMembers[START] ===============")
          (doseq [m future-map]
            (println (format "[%s]: value = %s" (. m getKey) (.. m getValue get))))
          (println "=============== submitToMembers[END] ==============="))

        (println)

        ;; submitToAllMembersで、クラスタ内の全Member
        (let [^Callable task (SumTask. map-name)
              ^Map future-map (. executorService submitToAllMembers task)]
          (println "=============== submitToAllMembers[START] ===============")
          (doseq [m future-map]
            (println (format "[%s]: value = %s" (. m getKey) (.. m getValue get))))
          (println "=============== submitToAllMembers[END] ==============="))))))

(defn- start-worker []
  (with-hazelcast
    (fn [hazelcast]
      (println (format "[%s] Start Worker..." (Date.)))
      (read-line))))

(defn -main
  [& args]
  (case (count args)
    0 (start-worker)
    (case (first args)
      "worker" (start-worker)
      "master" (start-master)
      (start-worker))))
