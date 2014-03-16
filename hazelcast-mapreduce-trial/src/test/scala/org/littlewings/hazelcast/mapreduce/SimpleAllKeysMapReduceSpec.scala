package org.littlewings.hazelcast.mapreduce

import com.hazelcast.core.ICompletableFuture
import com.hazelcast.mapreduce.{JobTracker, KeyValueSource}

import org.scalatest.{FunSpec, Entry}
import org.scalatest.Matchers._

class SimpleAllKeysMapReduceSpec extends FunSpec
                                 with HazelcastSpecSupport {
  describe("simple all mapreduce") {
    it("test map") {
      withHazelcast(2) { hazelcast =>
        val map = hazelcast.getMap[String, String]("simple-map")

        (1 to 100).foreach(i => map.put(s"key$i", s"value$i"))

        val source = KeyValueSource.fromMap(map)

        val jobTracker = hazelcast.getJobTracker("default")
        val job = jobTracker.newJob(source)

        val future: ICompletableFuture[java.util.Map[String, Int]] =
          job
            .mapper(new SimpleAllKeysMapper)
            .combiner(new SimpleAllKeysCombinerFactory)
            .reducer(new SimpleAllKeysReducerFactory)
            .submit

        println("!!!!!" + job.getClass)

        val result: java.util.Map[String, Int] = future.get

        result.get("key1") should be (1)
        result should contain (Entry("key1", 1))
        result should contain (Entry("key2", 1))
        result should have size 100
      }
    }

    it("test list") {
      withHazelcast(2) { hazelcast =>
        val list = hazelcast.getList[String]("simple-list")

        (1 to 100).foreach(i => list.add(s"entry$i"))

        val source = KeyValueSource.fromList(list)

        val jobTracker = hazelcast.getJobTracker("default")
        val job = jobTracker.newJob(source)

        val future: ICompletableFuture[java.util.Map[String, Int]] =
          job
            .mapper(new SimpleAllKeysMapper)
            .combiner(new SimpleAllKeysCombinerFactory)
            .reducer(new SimpleAllKeysReducerFactory)
            .submit

        val result: java.util.Map[String, Int] = future.get

        result should contain (Entry("simple-list", 100))
        result should have size 1
      }
    }
  }
}
