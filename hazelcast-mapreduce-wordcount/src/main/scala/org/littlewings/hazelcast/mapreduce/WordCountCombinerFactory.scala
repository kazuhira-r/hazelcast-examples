package org.littlewings.hazelcast.mapreduce

import com.hazelcast.mapreduce.{Combiner, CombinerFactory}

class WordCountCombinerFactory extends CombinerFactory[String, Long, Long] {
  override def newCombiner(key: String): Combiner[String, Long, Long] =
    new WordCountCombiner
}

class WordCountCombiner extends Combiner[String, Long, Long] {
  private[this] var count: Long = _

  override def combine(key: String, value: Long): Unit =
    count += value

  override def finalizeChunk: Long = {
    val c = count
    count = 0L
    c
  }
}
