package org.littlewings.hazelcast.sessionclustering

import java.io.IOException

import javax.servlet.ServletException
import javax.servlet.annotation.WebServlet
import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse, HttpSession}

@WebServlet(Array("/*"))
class SharedSessionServlet extends HttpServlet {
  @throws(classOf[IOException])
  @throws(classOf[ServletException])
  override protected def doGet(req: HttpServletRequest, res: HttpServletResponse): Unit = {
    val session = req.getSession

    val counter: Integer = session.getAttribute("counter").asInstanceOf[Integer]
    val nextVal: Integer =
      if (counter == null) 1 else counter + 1

    session.setAttribute("counter", nextVal)
    res.getWriter.println(s"Counter = $nextVal")
  }
}
