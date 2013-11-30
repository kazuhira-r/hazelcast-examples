(defproject hazelcast-continuous-query "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [com.hazelcast/hazelcast "3.1"]]
  :main ^:skip-aot hazelcast-continuous-query.core
  :target-path "target/%s"
  :aot :all
;;  :jvm-opts ["-Xmx1G"]
  :profiles {:uberjar {:aot :all}})
