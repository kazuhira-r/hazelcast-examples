package org.littlewings.hazelcast.mapstore

import org.scalatest.FunSpec
import org.scalatest.Matchers._

class NoMapStoreSpec extends FunSpec with HazelcastSpecSupport {
  describe("no map-store spec") {
    it("start put down, size") {
      withHazelcast { hazelcast =>
        val map = hazelcast.getMap[String, String]("default")

        (1 to 3).foreach(i => map.put(s"key$i", s"value$i"))

        map should have size 3
      }

      withHazelcast { hazelcast =>
        val map = hazelcast.getMap[String, String]("default")

        map should be ('empty)
      }
    }
  }
}
