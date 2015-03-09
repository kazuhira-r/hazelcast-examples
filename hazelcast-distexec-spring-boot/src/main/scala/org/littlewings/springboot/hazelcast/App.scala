package org.littlewings.springboot.hazelcast

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

import org.littlewings.springboot.hazelcast.config.AppConfig

object App {
  def main(args: Array[String]): Unit =
    SpringApplication.run(classOf[App], args: _*)
}

@SpringBootApplication
class App
