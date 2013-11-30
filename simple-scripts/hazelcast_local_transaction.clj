(require '[leiningen.exec :as exec])

(exec/deps '[[com.hazelcast/hazelcast "3.1.1"]])

(ns hazelcast-local-transaction
  (:import (com.hazelcast.config Config)
           (com.hazelcast.core Hazelcast HazelcastInstance IMap TransactionalMap)
           (com.hazelcast.transaction TransactionContext TransactionNotActiveException)
           (com.hazelcast.transaction TransactionOptions TransactionOptions$TransactionType)))


(try
  ;; HazelcastInstanceを作成する
  (let [^Config config (Config.)
        ^HazelcastInstance hazelcast (Hazelcast/newHazelcastInstance config)
        ;; ローカルトランザクションを使用するように設定
        ^TransactionOptions options (-> (TransactionOptions.)
                                        (.setTransactionType (TransactionOptions$TransactionType/LOCAL)))
        ^TransactionContext context (. hazelcast newTransactionContext options)]
    (try
      ;; トランザクションを開始せず、TransactionalMapを取得しようとすると
      ;; 例外となる
      (. context getMap "transactional-map")
      (catch TransactionNotActiveException e (println e)))


    ;; トランザクション開始
    (.. context beginTransaction)

    (let [^TransactionalMap transactional-map (. context getMap "transactional-map")]
      (. transactional-map put "key1" "value1")
      (. transactional-map put "key2" "value2")
      (. transactional-map put "key3" "value3"))

    ;; コミット
    (.. context commitTransaction)


    ;; 値を見る時には、IMapで
    (let [^IMap transactional-map (. hazelcast getMap "transactional-map")]
      (assert (= (. transactional-map get "key1") "value1"))
      (assert (. transactional-map containsKey "key2"))
      (assert (= (. transactional-map get "key3") "value3")))


    ;; ロールバックを試す
    (.. context beginTransaction)

    (let [^TransactionalMap transactional-map (. context getMap "transactional-map")]
      (. transactional-map put "key4" "value4")
      (. transactional-map put "key5" "value5")
      (. transactional-map put "key6" "value6"))

    ;; ロールバック
    (.. context rollbackTransaction)


    ;; IMapで確認しないと、TransactionalMapで見てしまうと値が見えてしまう模様
    (let [^IMap transactional-map (. hazelcast getMap "transactional-map")]
      (assert (nil? (. transactional-map get "key4")))
      (assert (false? (. transactional-map containsKey "key5")))
      (assert (nil? (. transactional-map get "key6"))))


    ;; TransactionalMapで見るために、トランザクションを開始
    (.. context beginTransaction)

    ;; TransactionalMapで見ると、ロールバックしても値が見えてしまう模様…
    (let [^TransactionalMap transactional-map (. context getMap "transactional-map")]
      (assert (= (. transactional-map get "key4") "value4"))
      (assert (true? (. transactional-map containsKey "key5")))
      (assert (= (. transactional-map get "key6") "value6")))

    ;; ロールバック
    (.. context rollbackTransaction)
    ;; コミットしようとすると、こんなエラーを見たりします…
    ;; (.. context commitTransaction)
    ;; java.lang.IllegalStateException: An operation[BasePutOperation{transactional-map}] can not be used for multiple invocations!


    ;; ちなみに、同じ名前でIMapにputするのはOKらしいです
    (let [^IMap transactional-map (. hazelcast getMap "transactional-map")]
      (. transactional-map put "key7" "value7"))

    ;; TransactionalMapで見るために、トランザクションを開始
    (.. context beginTransaction)
    ;; TransactionalMapからも、確認可能
    (let [^TransactionalMap transactional-map (. context getMap "transactional-map")]
      (assert (= (. transactional-map get "key7") "value7")))
    ;; ロールバック
    (.. context rollbackTransaction)


    ;; HazelcastInstanceをシャットダウンする
    (.. hazelcast getLifecycleService shutdown))

  ;; 全Hazelcastインスタンスをシャットダウンする
  (finally (Hazelcast/shutdownAll)))
