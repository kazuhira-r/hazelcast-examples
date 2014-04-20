package org.littlewings.hazelcast.affinity

import java.util.concurrent.Callable

import com.hazelcast.core.{Hazelcast, PartitionAware}

@SerialVersionUID(1L)
class DataAffinityTask(keyPrefix: String, range: Integer) extends Callable[Integer]
                                                          with PartitionAware[String]
                                                          with Serializable {
  @throws(classOf[Exception])
  override def call: Integer = {
    val threadName = Thread.currentThread.getName
    val instanceName = threadName.substring(3, threadName.indexOf('.', 3))
    val hazelcast = Hazelcast.getHazelcastInstanceByName(instanceName)

    val map = hazelcast.getMap[AffinityKey, Integer]("map")

    (1 to range).foldLeft(0) { (acc, i) =>
      acc + map.get(new AffinityKey(s"${keyPrefix}-$i"))
    }
  }

  override def getPartitionKey: String =
    keyPrefix
}
