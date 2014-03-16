package org.littlewings.hazelcast.mapreduce

import scala.collection.JavaConverters._
import scala.collection._
import scala.collection.immutable.TreeMap

import com.hazelcast.mapreduce.Collator

trait WordCountCollator extends Collator[java.util.Map.Entry[String, Long], Seq[(String, Long)]] {
  protected val topN: Int

  override def collate(values: java.lang.Iterable[java.util.Map.Entry[String, Long]]): Seq[(String, Long)] =
    values
      .asScala
      .foldLeft(mutable.ArrayBuffer.empty[(String, Long)]) { (acc, entry) =>
        acc += (entry.getKey -> entry.getValue)
        acc
      }
      .sortWith((a, b) => a._2 > b._2)
      .take(topN)
}

class WordCountTop10Collator extends WordCountCollator {
  val topN: Int = 10
}

class WordCountTop100Collator extends WordCountCollator {
  val topN: Int = 100
}

