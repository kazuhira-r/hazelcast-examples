package org.littlewings.springboot.hazelcast

import javax.servlet.http.HttpSession

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.{ RestController, RequestMapping }

@RestController
class SimpleController {
  @RequestMapping(Array("/hello"))
  def hello(session: HttpSession): String = {
    session.getAttribute("counter") match {
      case null =>
        session.setAttribute("counter", Integer.valueOf(1))
      case n: Integer =>
        session.setAttribute("counter", Integer.valueOf(n + 1))
    }

    session.getAttribute("counter").toString
  }
}
