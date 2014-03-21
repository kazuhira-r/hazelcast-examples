package org.littlewings.hazelcast.keydist

import scala.collection.JavaConverters._

import com.hazelcast.core.{Hazelcast, HazelcastInstance}
import com.hazelcast.instance.{HazelcastInstanceImpl, HazelcastInstanceProxy}
import com.hazelcast.map.{DefaultRecordStore, MapService, RecordStore}
import com.hazelcast.nio.serialization.{Data, SerializationConstants}
import com.hazelcast.spi.impl.NodeEngineImpl

import org.scalatest.FunSpec
import org.scalatest.Matchers._

class KeyDistributionSpec extends FunSpec {
  describe("key distribution spec") {
    it("range key, entry test") {
      withHazelcast(2) { hazelcast =>
        val map = hazelcast.getMap[String, String]("default")

        val entryRange = 1 to 20
        val entries = entryRange.map(i => s"key$i" -> s"value$i").toMap

        entries.foreach { case (k, v) => map.put(k, v) }

        val selfNode = hazelcast.getCluster.getLocalMember
        val partitionService = hazelcast.getPartitionService

        val selfPartitions =
          partitionService
            .getPartitions
            .asScala
            .withFilter(_.getOwner == selfNode)

        val nodeEngine = getNodeEngineImpl(hazelcast)
        val clusterService = nodeEngine.getClusterService
        val mapService: MapService = nodeEngine.getService(MapService.SERVICE_NAME)
        val serializationService = nodeEngine.getSerializationService
        
        selfPartitions
         .map { partition =>
          (partition, mapService.getRecordStore(partition.getPartitionId, "default"))
        }
        .withFilter(!_._2.isEmpty)
        .foreach { case (partition, recordStore) =>
          println(s"""|PartitionId[${partition.getPartitionId}]
                      |  Owner = ${partition.getOwner}
                      |  RecordStore:
                      |${recordStore.entrySetObject.asScala.mkString("    ", System.lineSeparator + "    ", "")}
                      |""".stripMargin)
        }

        entries.foreach { case (key, value) =>
          val partition = partitionService.getPartition(key)
 
         val recordStore: RecordStore =
            mapService.getRecordStore(partition.getPartitionId,
                                      "default")

          val keyData = serializationService.toData(key)
          val valueData = recordStore.get(keyData)

          serializationService.toObject(keyData) should be (key)
          serializationService.toObject(valueData.asInstanceOf[Data]) should be (value)
        }
      }
    }
  }

  private def getNodeEngineImpl(hazelcast: HazelcastInstance): NodeEngineImpl = {
    val method = classOf[HazelcastInstanceProxy].getDeclaredMethod("getOriginal")
    method.setAccessible(true)
    val original = method.invoke(hazelcast).asInstanceOf[HazelcastInstanceImpl]

    original.node.nodeEngine
  }

  def withHazelcast(instanceNumber: Int)(fun: HazelcastInstance => Unit): Unit = {
    val instances =
      (1 to instanceNumber).map(i => Hazelcast.newHazelcastInstance)

    try {
      fun(instances.head)
    } finally {
      Hazelcast.shutdownAll()
    }
  }
}
