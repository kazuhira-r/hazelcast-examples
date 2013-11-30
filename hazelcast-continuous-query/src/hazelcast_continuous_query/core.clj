(ns hazelcast-continuous-query.core
  (:gen-class)
  (:import (com.hazelcast.config Config)
           (com.hazelcast.core Hazelcast HazelcastInstance IMap)
           (com.hazelcast.query SqlPredicate)
           (hazelcast-continuous-query.book Book)
           (hazelcast-continuous-query.listener MyListener)))

(defn with-hazelcast [func]
  (try
    (let [^Config config (Config.)
          ^HazelcastInstance hazelcast (Hazelcast/newHazelcastInstance config)]
      (func hazelcast)
      ;; HazelcastInstanceをシャットダウン
      (.. hazelcast getLifecycleService shutdown))
    ;; 全Hazelcastインスタンスをシャットダウン
    (finally (Hazelcast/shutdownAll))))

(defn -main
  [& args]
  (with-hazelcast
    (fn [hazelcast]
      (let [^IMap book-map (. hazelcast getMap "book-map")]
        ;; Continuous Queryを使うために、EntryListenerを登録
        ;;(. book-map addEntryListener (MyListener.) (SqlPredicate. "price > 3500") true)
        ;;(. book-map addEntryListener (MyListener.) (SqlPredicate. "outOfPrint") true)
        ;; こちらは、普通のEntryListener
        ;;(. book-map addEntryListener (MyListener.) true)
        ;;(. book-map addEntryListener (MyListener.) "978-1782167303" true)
        (. book-map addEntryListener (MyListener.) (SqlPredicate. "price > 3500") "978-1782167303" true)

        ;; 適当に書籍を登録
        (. book-map put "978-1782167303" (Book. {:isbn13 "978-1782167303"
                                                 :name "Getting Started with Hazelcast"
                                                 :price 4147
                                                 :publish-date "2013-08-27"
                                                 :category "IMDG"
                                                 :out-of-print false}))
        (. book-map put "978-1849518222" (Book. {:isbn13 "978-1849518222"
                                                 :name "Infinispan Data Grid Platform"
                                                 :price 3115
                                                 :publish-date "2012-06-30"
                                                 :category "IMDG"
                                                 :out-of-print false}))
        (. book-map put "978-4274069130" (Book. {:isbn13 "978-4274069130"
                                                 :name "プログラミングClojure 第2版"
                                                 :price 3570
                                                 :publish-date "2013-04-26"
                                                 :category "Clojure"
                                                 :out-of-print false}))
        (. book-map put "978-4774159911" (Book. {:isbn13 "978-4774159911"
                                                 :name "おいしいClojure入門"
                                                 :price 2919
                                                 :publish-date "2013-09-26"
                                                 :category "Clojure"
                                                 :out-of-print false}))
        (. book-map put "978-4774127804" (Book. {:isbn13 "978-4774127804"
                                                 :name "Apache Lucene 入門 ～Java・オープンソース・全文検索システムの構築"
                                                 :price 3360
                                                 :publish-date "2006-05-17"
                                                 :category "FullTextSearch"
                                                 :out-of-print true}))
        (. book-map put "978-4774141756" (Book. {:isbn13 "978-4774141756"
                                                 :name "Apache Solr入門 ―オープンソース全文検索エンジン"
                                                 :price 3780
                                                 :publish-date "2010-02-20"
                                                 :category "FullTextSearch"
                                                 :out-of-print false}))

        (Thread/sleep 3000)))))

