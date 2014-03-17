package org.littlewings.hazelcast.mapreduce

import com.hazelcast.mapreduce.{Reducer, ReducerFactory}

@SerialVersionUID(1L)
class WordCountReducerFactory extends ReducerFactory[String, Long, Long] {
  override def newReducer(key: String): Reducer[String, Long, Long] =
    new WordCountReducer
}

class WordCountReducer extends Reducer[String, Long, Long] {
  private[this] var count: Long = _

  override def reduce(value: Long): Unit =
    count += value

  override def finalizeReduce: Long =
    count
}
