package org.littlewings.hazelcast.mapstore

import org.scalatest.FunSpec
import org.scalatest.Matchers._

class EasyFileMapStoreSpec extends FunSpec with HazelcastSpecSupport {
  describe("easy file map-store spec") {
    it("clear") {
      withHazelcast { hazelcast =>
        val map = hazelcast.getMap[String, String]("easy-file-mapstore-map")

        map.clear()
      }
    }

    it("put-load") {
      withHazelcast { hazelcast =>
        val map = hazelcast.getMap[String, String]("easy-file-mapstore-map")

        (1 to 3).foreach(i => map.put(s"key$i", s"value$i"))
      }

      withHazelcast { hazelcast =>
        val map = hazelcast.getMap[String, String]("easy-file-mapstore-map")

        map.keySet should contain only ("key1", "key2", "key3")
        map.values should contain only ("value1", "value2", "value3")
      }
    }
  }
}
