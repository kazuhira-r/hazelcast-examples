package org.littlewings.hazelcast.mapreduce

import scala.collection.JavaConverters._

import com.hazelcast.core.ICompletableFuture
import com.hazelcast.mapreduce.{JobTracker, KeyValueSource}

import org.scalatest.{FunSpec, Entry}
import org.scalatest.Matchers._

class ThreadInspectMapReduceSpec extends FunSpec
                                 with HazelcastSpecSupport {
  describe("thread inspect mapreduce") {
    it("test map") {
      withHazelcast(2) { hazelcast =>
        val map = hazelcast.getMap[String, String]("simple-map")

        (1 to 1000).foreach(i => map.put(s"key$i", s"value$i"))

        val source = KeyValueSource.fromMap(map)

        val jobTracker = hazelcast.getJobTracker("default")
        val job = jobTracker.newJob(source)

        val future: ICompletableFuture[java.util.Map[String, Iterable[String]]] =
          job
            .mapper(new ThreadInspectMapper)
            .combiner(new ThreadInspectCombinerFactory)
            .reducer(new ThreadInspectReducerFactory)
            .submit

        val result: java.util.Map[String, Iterable[String]] = future.get

        //result.asScala.foreach(println)
      }
    }
  }
}
