(ns hazelcast-query.core
  (:gen-class)
  (:import (com.hazelcast.config Config MapConfig MapIndexConfig)
           (com.hazelcast.core Hazelcast HazelcastInstance IMap)
           (com.hazelcast.query EntryObject Predicate PredicateBuilder SqlPredicate)
           (hazelcast-query.book Book)))

(defn -main
  [& args]
  (try
    (let [^Config config (Config.)]
      ;; インデックスの設定は、MapConfigとMapIndexConfigで可能
      (. config addMapConfig (-> (MapConfig. "book-map")
                                 (.addMapIndexConfig (MapIndexConfig."price" true)))) ;; 順序あり
      (let [^HazelcastInstance hazelcast (Hazelcast/newHazelcastInstance config)
            ^IMap book-map (. hazelcast getMap "book-map")]
        ;; IMapに対しても、設定可能
        ;;(. book-map addIndex "price" true)  ;; 順序あり
        (. book-map addIndex "publishDate" true)  ;; 順序あり
        (. book-map addIndex "outOfPrint" false)  ;; 順序なし
        (. book-map addIndex "category" false)  ;; 順序なし

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
                                                 :name "Apache Lucene 入門 〜Java・オープンソース・全文検索システムの構築"
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

        ;; Queryを実行
        (println "====== Interactive Query Start =====")
        (doseq [query-string (take-while #(not (= "exit" %))
                                         (repeatedly #(do (print "Query> ")
                                                          (flush)
                                                          (read-line))))]
          (when (and (not (nil? query-string))
                     (not (empty? query-string)))
            (try
              (println (format "Input Query => [%s]" query-string))
              (let [pred (SqlPredicate. query-string)]
                (doseq [v (. book-map values pred)] ;; keySet、entrySetなどにも使用可能
                  (println (format "  Result: %s" v))))
              (catch Exception e
                (println (format "Bad Query[%s], Reason[%s]" query-string e))))))
        (println "====== Interactive Query End =====")

        ;; Criteria APIを使う
        (println "====== Using Criteria API =====")
        ;; 下記で、「outOfPrint = false AND price >= 3000 AND category = 'Clojure'」と同じ
        (let [^EntryObject eo (. (PredicateBuilder.) getEntryObject)
              ^Predicate pred (-> eo
                                  (.isNot "outOfPrint")
                                  (.and (-> eo
                                            (.get "price")
                                            (.greaterEqual 3000)))
                                  (.and (-> eo
                                            (.get "category")
                                            (.equal "Clojure"))))]
          (doseq [v (. book-map values pred)]
            (println (format "  Result: %s" v))))

        ;; HazelcastInstanceをシャットダウン
        (.. hazelcast getLifecycleService shutdown)))

    ;; 全Hazelcastインスタンスをシャットダウン
    (finally (Hazelcast/shutdownAll))))


