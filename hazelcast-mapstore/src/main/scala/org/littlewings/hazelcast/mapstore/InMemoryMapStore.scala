package org.littlewings.hazelcast.mapstore

import scala.collection._
import scala.collection.JavaConverters._
import scala.collection.concurrent.TrieMap

import java.util.Collection

import com.hazelcast.core.MapStore

class InMemoryMapStore extends MapStore[String, String] {
  val store: mutable.Map[String, String] = new TrieMap[String, String]

  /* MapStoreのメソッドを実装 */
  override def delete(key: String): Unit =
    store -= key

  override def deleteAll(keys: Collection[String]): Unit =
    store --= keys.asScala

  override def store(key: String, value: String): Unit =
    store += (key -> value)

  override def storeAll(map: java.util.Map[String, String]): Unit =
    store ++= map.asScala

  /* MapLoaderのメソッドを実装 */
  override def load(key: String): String =
    store.get(key).getOrElse(null)

  override def loadAll(keys: Collection[String]): java.util.Map[String, String] =
    keys
      .asScala
      .withFilter(k => store.contains(k))
      .map(k => (k -> store.get(k).getOrElse(null)))
      .toMap
      .asJava

  override def loadAllKeys: java.util.Set[String] =
    store.keySet.asJava
}
