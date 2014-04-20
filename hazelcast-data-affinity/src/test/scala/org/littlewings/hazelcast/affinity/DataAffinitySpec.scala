package org.littlewings.hazelcast.affinity

import com.hazelcast.core.{Hazelcast, HazelcastInstance, Partition}

import org.scalatest.FunSpec
import org.scalatest.Matchers._

class DataAffinitySpec extends FunSpec {
  describe("Affinity Key") {
    it("prefix key") {
      new AffinityKey("key1-1").getPartitionKey should be ("key1")
      new AffinityKey("key1-2").getPartitionKey should be ("key1")
      new AffinityKey("key2-1").getPartitionKey should be ("key2")
      new AffinityKey("key2-2").getPartitionKey should be ("key2")
    }
  }

  describe("Data Affinity") {
    it("no affinity key") {
      val instanceNumber = 4

      withHazelcast(instanceNumber) { hazelcast =>
        val partitionService = hazelcast.getPartitionService

        val map1 = hazelcast.getMap[String, Integer]("map1")
        val map2 = hazelcast.getMap[String, Integer]("map2")
        val map3 = hazelcast.getMap[String, Integer]("map3")

        val keysValues =
          for {
            i <- 1 to 5
            j <- 1 to 5
          } yield (s"key$i-$j", i * j)

        keysValues.foreach { case (key, value) =>
          Array(map1, map2, map3).foreach(_.put(key, value))
        }

        keysValues.foreach { case (key, _) =>
          println(s"[No Affinity Key] key = $key, owner = ${partitionService.getPartition(key).getOwner}")
        }
      }
    }

    it("affinity key") {
      val instanceNumber = 4

      withHazelcast(instanceNumber) { hazelcast =>
        val partitionService = hazelcast.getPartitionService

        val map1 = hazelcast.getMap[AffinityKey, Integer]("map1")
        val map2 = hazelcast.getMap[AffinityKey, Integer]("map2")
        val map3 = hazelcast.getMap[AffinityKey, Integer]("map3")

        val keysValues =
          for {
            i <- 1 to 5
            j <- 1 to 5
          } yield (s"key$i-$j", i * j)

        keysValues.foreach { case (key, value) =>
          Array(map1, map2, map3).foreach(_.put(new AffinityKey(key), value))
        }

        keysValues.foreach { case (key, _) =>
          println(s"[Affinity Key] key = $key, owner = ${partitionService.getPartition(new AffinityKey(key)).getOwner}")
        }

        def getPartition(key: String): Partition =
          partitionService.getPartition(new AffinityKey(key))

        val partition1 = getPartition("key1-1")
        getPartition("key1-2").getOwner should be (partition1.getOwner)
        getPartition("key1-3").getOwner should be (partition1.getOwner)
        getPartition("key1-4").getOwner should be (partition1.getOwner)
        getPartition("key1-5").getOwner should be (partition1.getOwner)

        val partition2 = getPartition("key2-1")
        getPartition("key2-2").getOwner should be (partition2.getOwner)
        getPartition("key2-3").getOwner should be (partition2.getOwner)
        getPartition("key2-4").getOwner should be (partition2.getOwner)
        getPartition("key2-5").getOwner should be (partition2.getOwner)
      }
    }
  }

  describe("Distributed Executor") {
    it("execute") {
      val instanceNumber = 4

      withHazelcast(instanceNumber) { hazelcast =>
        val partitionService = hazelcast.getPartitionService

        val map = hazelcast.getMap[AffinityKey, Integer]("map")

        val keysValues =
          for {
            i <- 1 to 5
            j <- 1 to 5
          } yield (s"key$i-$j", i * j)

        keysValues.foreach { case (key, value) =>
          map.put(new AffinityKey(key), value)
        }

        val executor = hazelcast.getExecutorService("default")

        val futures =
          Array(executor.submit(new DataAffinityTask("key1", 5)),
                executor.submit(new DataAffinityTask("key2", 5)),
                executor.submit(new DataAffinityTask("key3", 5)),
                executor.submit(new DataAffinityTask("key4", 5)),
                executor.submit(new DataAffinityTask("key5", 5)))

        futures.map(_.get) should contain theSameElementsInOrderAs Array(15, 30, 45, 60, 75)
      }
    }
  }

  private def withHazelcast(instanceNumber: Int)(fun: HazelcastInstance => Unit): Unit = {
    val instances = (1 to instanceNumber).map(_ => Hazelcast.newHazelcastInstance)

    try {
      fun(instances.head)
    } finally {
      instances.foreach(_.getLifecycleService.shutdown())

      Hazelcast.shutdownAll()
    }
  }
}
