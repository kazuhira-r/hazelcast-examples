package org.littlewings.hazelcast.mapstore

import scala.collection._
import scala.collection.JavaConverters._

import java.io.{ObjectInputStream, ObjectOutputStream}
import java.nio.file.{Files, Paths}
import java.util.Collection
import java.util.concurrent.ConcurrentHashMap

import com.hazelcast.core.MapStore

class EasyFileMapStore extends MapStore[String, String] {
  var store: mutable.Map[String, String] = new mutable.HashMap

  /* MapStoreのメソッドを実装 */
  override def delete(key: String): Unit = synchronized {
    store -= key
    persistStore()
  }

  override def deleteAll(keys: Collection[String]): Unit = synchronized {
    keys.asScala.foreach(k => store -= k)
    persistStore()
  }

  override def store(key: String, value: String): Unit = synchronized {
    store += (key -> value)
    persistStore()
  }

  override def storeAll(map: java.util.Map[String, String]): Unit = synchronized {
    store ++= map.asScala
    persistStore()
  }

  /* MapLoaderのメソッドを実装 */
  override def load(key: String): String = synchronized {
    loadStore()
    store.get(key).getOrElse(null)
  }

  override def loadAll(keys: Collection[String]): java.util.Map[String, String] = synchronized {
    loadStore()
    keys
      .asScala
      .withFilter(k => store.contains(k))
      .map(k => (k -> store.get(k).getOrElse(null)))
      .toMap
      .asJava
  }

  override def loadAllKeys: java.util.Set[String] = synchronized {
    loadStore()
    store.keySet.asJava
  }

  private def persistStore(): Unit = {
    val out = new ObjectOutputStream(Files.newOutputStream(Paths.get("store.ser")))
    try {
      out.writeObject(store)
    } finally {
      out.close()
    }
  }

  private def loadStore(): Unit = {
    val path = Paths.get("store.ser")
    if (Files.exists(path)) {
      val in = new ObjectInputStream(Files.newInputStream(path))
      try {
        store = in.readObject().asInstanceOf[mutable.Map[String, String]]
      } finally {
        in.close()
      }
    }
  }
}
