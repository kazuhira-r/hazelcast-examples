package org.littlewings.hazelcast.mapreduce

import scala.collection.JavaConverters._

import java.io.InputStream

import com.hazelcast.config.ClasspathXmlConfig
import com.hazelcast.core.{Hazelcast, HazelcastInstance}
import com.hazelcast.mapreduce.{JobTracker, KeyValueSource}

import dispatch._
import dispatch.Defaults._
import org.jsoup.Jsoup

import org.scalatest.FunSpec
import org.scalatest.Matchers._

class WordCountMapReduceSpec extends FunSpec {
  describe("word count map-reduce spec") {
    it("word count top10") {
      withHazelcast(2) { hazelcast =>
        val list = hazelcast.getList[String]("default")

        val http = Http
        val document =
          try {
            import com.ning.http.client
            object ByteStream extends (client.Response => InputStream) {
              def apply(r: client.Response): InputStream =
                r.getResponseBodyAsStream
            }

            val request = url("http://www.aozora.gr.jp/cards/000148/files/752_14964.html")
            Jsoup.parse(http(request OK ByteStream).apply(),
                        "Windows-31J",
                        "")
          } finally {
            http.shutdown()
          }

        document
          .select("div.main_text")
          .asScala
          .map(_.text)
          .foreach(_.split("。").foreach(list.add))

        val jobTracker = hazelcast.getJobTracker("default")
        val source = KeyValueSource.fromList(list)
        val job = jobTracker.newJob(source)

        val future =
          job
            .mapper(new TokenizeMapper)
            .combiner(new WordCountCombinerFactory)
            .reducer(new WordCountReducerFactory)
            .submit(new WordCountTop10Collator)

        val result = future.get

        result should have size (10)
        result should contain theSameElementsAs Seq(
          ("おれ", 472),
          ("事", 291),
          ("人", 213),
          ("君", 184),
          ("赤" ,178),
          ("一", 176),
          ("シャツ",170),
          ("山嵐", 155),
          ("何", 144),
          ("二", 121)
        )
      }
    }
  }

  def withHazelcast(instanceNumber: Int)(fun: HazelcastInstance => Unit): Unit = {
    val instances = 
    (1 to instanceNumber)
      .map(i => Hazelcast.newHazelcastInstance(new ClasspathXmlConfig("hazelcast.xml")))

    try {
      try {
        fun(instances.head)
      } finally {
        instances.foreach(_.getLifecycleService.shutdown())
      }
    } finally {
      Hazelcast.shutdownAll()
    }
  }
}
