package org.littlewings.springboot.hazelcast

import javax.servlet.DispatcherType
import javax.servlet.http.HttpSessionListener

import com.hazelcast.web.{ SessionListener, WebFilter }
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.context.embedded.FilterRegistrationBean
import org.springframework.context.annotation.{ Bean, ComponentScan }
import org.springframework.core.Ordered

object App {
  def main(args: Array[String]): Unit = SpringApplication.run(classOf[App], args: _*)
}

@EnableAutoConfiguration
@ComponentScan
class App {
  @Bean
  def hazelcastWmFilter: FilterRegistrationBean = {
    val registration = new FilterRegistrationBean
    registration.setFilter(new WebFilter)
    registration.addInitParameter("instance-name", "spring-boot-hazelcast-wm")
    registration.addInitParameter("session-ttl-seconds", "3600")
    registration.addInitParameter("sticky-session", "false")
    registration.addInitParameter("deferred-write", "false")
    registration.addUrlPatterns("/*")  // 何も指定しない場合は、これと一緒？
    registration.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE)
    registration.setOrder(Ordered.HIGHEST_PRECEDENCE)
    registration
  }

  @Bean
  def hazelcastSessionListener: HttpSessionListener = new SessionListener
}
