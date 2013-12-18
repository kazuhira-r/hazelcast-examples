package org.littlewings.hazelcast.entrylistener

import com.hazelcast.config.Config
import com.hazelcast.core.{Hazelcast, HazelcastInstance}

import org.scalatest.FunSpec
import org.scalatest.Matchers._

class EntryListenerSpec extends FunSpec {
  describe("entry listener spec") {
    it("add event") {
      withHazelcast { hazelcast =>
        val listener = new CounteredEntryListener
        val map = hazelcast.getMap[String, String]("default")
        map.addEntryListener(listener, false)

        map.put("key1", "value1")

        Thread.sleep(1 * 1000L)

        listener.added should be (1)
        listener.updated should be (0)
        listener.removed should be (0)
        listener.evicted should be (0)
      }
    }

    it("update event #1") {
      withHazelcast { hazelcast =>
        val listener = new CounteredEntryListener
        val map = hazelcast.getMap[String, String]("default")
        map.addEntryListener(listener, false)

        map.put("key1", "value1")
        map.put("key1", "value1-1")

        Thread.sleep(1 * 1000L)

        listener.added should be (1)
        listener.updated should be (1)
        listener.removed should be (0)
        listener.evicted should be (0)
      }
    }

    it("update event #2") {
      withHazelcast { hazelcast =>
        val listener = new CounteredEntryListener
        val map = hazelcast.getMap[String, String]("default")
        map.addEntryListener(listener, false)

        map.put("key1", "value1")
        map.replace("key1", "value1-1")

        Thread.sleep(1 * 1000L)

        listener.added should be (1)
        listener.updated should be (1)
        listener.removed should be (0)
        listener.evicted should be (0)
      }
    }

    it("remove event") {
      withHazelcast { hazelcast =>
        val listener = new CounteredEntryListener
        val map = hazelcast.getMap[String, String]("default")
        map.addEntryListener(listener, false)

        map.put("key1", "value1")
        map.remove("key1")

        Thread.sleep(1 * 1000L)

        listener.added should be (1)
        listener.updated should be (0)
        listener.removed should be (1)
        listener.evicted should be (0)
      }
    }

    it("evict event") {
      withHazelcast { hazelcast =>
        val listener = new CounteredEntryListener
        val map = hazelcast.getMap[String, String]("default")
        map.addEntryListener(listener, false)

        map.put("key1", "value1")
        map.evict("key1")

        Thread.sleep(1 * 1000L)

        listener.added should be (1)
        listener.updated should be (0)
        listener.removed should be (0)
        listener.evicted should be (1)
      }
    }
  }

  def withHazelcast(fun: HazelcastInstance => Unit): Unit = {
    val hazelcast = Hazelcast.newHazelcastInstance(new Config)

    try {
      fun(hazelcast)
    } finally {
      hazelcast.getLifecycleService.shutdown
    }
  }
}
