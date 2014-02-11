package org.littlewings.hazelcast.mapstore

import com.hazelcast.core.{MapLoader, MapStoreFactory}

object InMemoryMapStoreFactory {
  val INSTANCE: InMemoryMapStore = new InMemoryMapStore
}

class InMemoryMapStoreFactory extends MapStoreFactory[String, String] {
  override def newMapStore(mapName: String, properties: java.util.Properties): MapLoader[String, String] =
    InMemoryMapStoreFactory.INSTANCE
}
