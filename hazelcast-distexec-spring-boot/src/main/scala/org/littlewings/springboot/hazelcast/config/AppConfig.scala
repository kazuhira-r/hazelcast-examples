package org.littlewings.springboot.hazelcast.config

import com.hazelcast.config.Config
import com.hazelcast.core.{ Hazelcast, HazelcastInstance }
import com.hazelcast.spring.context.SpringManagedContext
import org.springframework.context.annotation.{ Bean, Configuration }

@Configuration
class AppConfig {
  @Bean
  def springManagedContext: SpringManagedContext = new SpringManagedContext

  @Bean
  def hazelcast: HazelcastInstance = {
    val config = new Config
    config.setManagedContext(springManagedContext)
    Hazelcast.newHazelcastInstance(config)
  }
}
