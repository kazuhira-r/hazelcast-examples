package org.littlewings.hazelcast.mapreduce

import com.hazelcast.mapreduce.{Combiner, CombinerFactory, Context, Mapper, Reducer, ReducerFactory}

@SerialVersionUID(1L)
class ThreadInspectMapper extends Mapper[String, String, String, String] {
  override def map(key: String, value: String, context: Context[String, String]): Unit =
    context.emit(key, Thread.currentThread.getName + "[mapper]")
}

@SerialVersionUID(1L)
class ThreadInspectCombinerFactory extends CombinerFactory[String, String, Iterable[String]] {
  override def newCombiner(key: String): Combiner[String, String, Iterable[String]] =
    new ThreadInspectCombiner
}

class ThreadInspectCombiner extends Combiner[String, String, Iterable[String]] {
  private[this] var names: Set[String] = Set.empty

  override def combine(key: String, value: String): Unit = {
    names = names + value
    names = names + (Thread.currentThread.getName + "[combiner]")
  }

  override def finalizeChunk: Iterable[String] = {
    val s = names
    names = Set.empty
    s
  }
}

@SerialVersionUID(1L)
class ThreadInspectReducerFactory extends ReducerFactory[String, Iterable[String], Iterable[String]] {
  override def newReducer(key: String): Reducer[String, Iterable[String], Iterable[String]] =
    new ThreadInspectReducer
}

class ThreadInspectReducer extends Reducer[String, Iterable[String], Iterable[String]] {
  private[this] var names: Set[String] = Set.empty

  override def reduce(values: Iterable[String]): Unit = {
    names = names ++ values
    names = names + (Thread.currentThread.getName + "[reducer]")
  }

  override def finalizeReduce: Iterable[String] =
    names
}
