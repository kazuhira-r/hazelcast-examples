package org.littlewings.hazelcast.mapstore

import org.scalatest.FunSpec
import org.scalatest.Matchers._

class InMemoryMapStoreSpec extends FunSpec with HazelcastSpecSupport {
  describe("in memory map-store spec") {
    it("simple") {
      withHazelcast { hazelcast =>
        val map = hazelcast.getMap("in-memory-mapstore-map")

        val instance = InMemoryMapStoreFactory.INSTANCE
        instance.store should be ('empty)
      }
    }

    it("put") {
      withHazelcast { hazelcast =>
        val map = hazelcast.getMap[String, String]("in-memory-mapstore-map")

        (1 to 3).foreach(i => map.put(s"key$i", s"value$i"))

        val instance = InMemoryMapStoreFactory.INSTANCE
        instance.store should have size 3
        instance.store should contain only (("key1" -> "value1"),
                                            ("key2" -> "value2"),
                                            ("key3" -> "value3"))
      }
    }

    it("put-remove") {
      withHazelcast { hazelcast =>
        val map = hazelcast.getMap[String, String]("in-memory-mapstore-map")

        (1 to 3).foreach(i => map.put(s"key$i", s"value$i"))

        val instance = InMemoryMapStoreFactory.INSTANCE
        instance.store should have size 3
        instance.store should contain only (("key1" -> "value1"),
                                            ("key2" -> "value2"),
                                            ("key3" -> "value3"))

        (1 to 3).foreach(i => map.remove(s"key$i"))

        instance.store should be ('empty)
      }
    }
  }
}
