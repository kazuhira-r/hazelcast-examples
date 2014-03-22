package org.littlewings.hazelcast.mapreduce

import com.hazelcast.mapreduce.{Combiner, CombinerFactory, Context, Mapper, Reducer, ReducerFactory}

@SerialVersionUID(1L)
class ThreadInspectMapper extends Mapper[String, String, String, String] {
  override def map(key: String, value: String, context: Context[String, String]): Unit =
    context.emit("threads", Thread.currentThread.getName)
}

@SerialVersionUID(1L)
class ThreadInspectCombinerFactory extends CombinerFactory[String, String, Map[String, Set[String]]] {
  override def newCombiner(key: String): Combiner[String, String, Map[String, Set[String]]] =
    new ThreadInspectCombiner
}

class ThreadInspectCombiner extends Combiner[String, String, Map[String, Set[String]]] {
  private[this] var names: Map[String, Set[String]] = Map.empty

  override def combine(key: String, value: String): Unit = {
    names += ("mapper" -> (names.get("mapper").getOrElse(Set.empty) + value))
    names += ("combiner" -> (names.get("combiner").getOrElse(Set.empty) + Thread.currentThread.getName))
  }

  override def finalizeChunk: Map[String, Set[String]] = {
    val m = names
    names = Map.empty
    m
  }
}

@SerialVersionUID(1L)
class ThreadInspectReducerFactory extends ReducerFactory[String, Map[String, Set[String]], Map[String, Set[String]]] {
  override def newReducer(key: String): Reducer[String, Map[String, Set[String]], Map[String, Set[String]]] =
    new ThreadInspectReducer
}

class ThreadInspectReducer extends Reducer[String, Map[String, Set[String]], Map[String, Set[String]]] {
  private[this] var names: Map[String, Set[String]] = Map.empty

  override def reduce(values: Map[String, Set[String]]): Unit = {
    values.foreach { case (k, v) =>
      names += (k -> (names.get(k).getOrElse(Set.empty) ++ v))
    }
    names += ("reducer" -> (names.get("reducer").getOrElse(Set.empty) + Thread.currentThread.getName))
  }

  override def finalizeReduce: Map[String, Set[String]] =
    names
}
