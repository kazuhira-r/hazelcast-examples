package org.littlewings.springboot.hazelcast.rest

import scala.collection.JavaConverters._

import com.hazelcast.core.HazelcastInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.{ RestController, RequestMapping, RequestMethod, RequestParam }

import org.littlewings.springboot.hazelcast.service.MessageCallable

@RestController
class DistExecController @Autowired()(private val hazelcast: HazelcastInstance) {
  @RequestMapping(Array("/distexec"))
  def distexec(@RequestParam message: String): String = {
    val executor = hazelcast.getExecutorService("default")
    val callable = new MessageCallable(message)

    val result = executor.submitToAllMembers(callable)
    result
      .asScala
      .map { case (k, v) => s"$k: ${v.get}" }
      .mkString(System.lineSeparator)
  }
}
