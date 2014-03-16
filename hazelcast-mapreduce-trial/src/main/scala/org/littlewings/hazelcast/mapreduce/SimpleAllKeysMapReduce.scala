package org.littlewings.hazelcast.mapreduce

import com.hazelcast.mapreduce.{Combiner, CombinerFactory, Context, Mapper, Reducer, ReducerFactory}

class SimpleAllKeysMapper extends Mapper[String, String, String, Int] {
  override def map(key: String, value: String, context: Context[String, Int]): Unit =
    context.emit(key, 1)
}

class SimpleAllKeysCombinerFactory extends CombinerFactory[String, Int, Int] {
  override def newCombiner(key: String): Combiner[String, Int, Int] =
    new SimpleAllKeysCombiner
}

class SimpleAllKeysCombiner extends Combiner[String, Int, Int] {
  private[this] var sum: Int = _

  override def combine(key: String, value: Int): Unit =
    sum += value

  override def finalizeChunk: Int = {
    val s = sum
    sum = 0
    s
  }
}

class SimpleAllKeysReducerFactory extends ReducerFactory[String, Int, Int] {
  override def newReducer(key: String): Reducer[String, Int, Int] =
    new SimpleAllKeysReducer
}

class SimpleAllKeysReducer extends Reducer[String, Int, Int] {
  private[this] var sum: Int = _

  override def reduce(value: Int): Unit =
    sum += value

  override def finalizeReduce: Int =
    sum
}
