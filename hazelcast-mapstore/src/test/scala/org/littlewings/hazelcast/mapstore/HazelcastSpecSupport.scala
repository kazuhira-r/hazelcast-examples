package org.littlewings.hazelcast.mapstore

import com.hazelcast.config.ClasspathXmlConfig
import com.hazelcast.core.{Hazelcast, HazelcastInstance}

import org.scalatest.{BeforeAndAfterAll, Suite}

trait HazelcastSpecSupport extends BeforeAndAfterAll {
  this: Suite =>

  protected def withHazelcast[T](fun: HazelcastInstance => T): T = {
    val hazelcast =
      Hazelcast.newHazelcastInstance(new ClasspathXmlConfig("hazelcast.xml"))

    try {
      fun(hazelcast)
    } finally {
      hazelcast.getLifecycleService.shutdown()
    }
  }

  override def afterAll(): Unit =
    Hazelcast.shutdownAll()
}
