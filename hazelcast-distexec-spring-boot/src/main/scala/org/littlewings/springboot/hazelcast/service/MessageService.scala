package org.littlewings.springboot.hazelcast.service

import org.springframework.stereotype.Service

@Service
class MessageService {
  def decorate(message: String) = s"*** $message ***"
}
