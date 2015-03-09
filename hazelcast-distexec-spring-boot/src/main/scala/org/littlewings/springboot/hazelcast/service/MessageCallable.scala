package org.littlewings.springboot.hazelcast.service

import java.util.concurrent.Callable

import com.hazelcast.spring.context.SpringAware
import org.springframework.beans.factory.annotation.Autowired

@SpringAware
@SerialVersionUID(1L)
class MessageCallable(val message: String) extends Callable[String] with Serializable {
  @transient
  @Autowired
  private var messageService: MessageService = _

  @throws(classOf[Exception])
  override def call(): String = messageService.decorate(message)
}
