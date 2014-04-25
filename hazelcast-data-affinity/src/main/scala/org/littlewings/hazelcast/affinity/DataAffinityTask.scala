package org.littlewings.hazelcast.affinity

import java.util.concurrent.Callable

import com.hazelcast.core.{Hazelcast, HazelcastInstance, HazelcastInstanceAware, PartitionAware}

@SerialVersionUID(1L)
class DataAffinityTask(keyPrefix: String, range: Integer) extends Callable[Integer]
                                                          with PartitionAware[String]
                                                          with HazelcastInstanceAware
                                                          with Serializable {
  private var hazelcast: HazelcastInstance = _

  override def setHazelcastInstance(hazelcast: HazelcastInstance): Unit =
    this.hazelcast = hazelcast

  @throws(classOf[Exception])
  override def call: Integer = {
    val map = hazelcast.getMap[AffinityKey, Integer]("map")

    (1 to range).foldLeft(0) { (acc, i) =>
      acc + map.get(new AffinityKey(s"${keyPrefix}-$i"))
    }
  }

  override def getPartitionKey: String =
    keyPrefix
}
