package org.littlewings.hazelcast.mapreduce

import com.hazelcast.config.ClasspathXmlConfig
import com.hazelcast.core.{Hazelcast, HazelcastInstance}

import org.scalatest.Suite

trait HazelcastSpecSupport {
  this: Suite =>

  protected def withHazelcast(n: Int)(fun: HazelcastInstance => Unit): Unit = {
    val instances =
      (1 to n).map(i => Hazelcast.newHazelcastInstance(new ClasspathXmlConfig("hazelcast.xml")))

    try {
      fun(instances.head)
    } finally {
      instances.foreach(_.getLifecycleService.shutdown())
    }
  }
}
