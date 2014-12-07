package org.littlewings.springboot.hazelcast

import javax.servlet.http.HttpSession

import org.springframework.stereotype.Controller
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.{ RestController, RequestMapping }

@RestController
class SimpleController @Autowired() (private val session: HttpSession) {
  @RequestMapping(Array("/hello"))
  def hello: String = {
    session.getAttribute("counter") match {
      case null =>
        session.setAttribute("counter", Integer.valueOf(1))
      case n: Integer =>
        session.setAttribute("counter", Integer.valueOf(n + 1))
    }

    session.getAttribute("counter").toString
  }
}
