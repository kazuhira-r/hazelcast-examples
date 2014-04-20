package org.littlewings.hazelcast.affinity

import com.hazelcast.core.PartitionAware

@SerialVersionUID(1L)
final class AffinityKey(val key: String) extends Serializable
                                         with PartitionAware[String] {
  override def getPartitionKey: String =
    key.substring(0, key.indexOf('-'))
} 
