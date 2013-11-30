(defproject hazelcast-entry-processor "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [com.hazelcast/hazelcast "3.1"]]
  :main hazelcast-entry-processor.core
  :aot :all
  :profiles {:uberjar {:aot :all}})
