package org.littlewings.hazelcast.spring;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.hazelcast.HazelcastKeyValueAdapter;
import org.springframework.data.hazelcast.repository.config.EnableHazelcastRepositories;
import org.springframework.data.keyvalue.core.KeyValueOperations;
import org.springframework.data.keyvalue.core.KeyValueTemplate;

@Configuration
@EnableHazelcastRepositories
public class HazelcastConfig {
    @Bean(destroyMethod = "shutdown")
    public HazelcastInstance hazelcastInstance() {
        return Hazelcast.newHazelcastInstance();
    }

    @Bean
    public KeyValueOperations keyValueTemplate(HazelcastKeyValueAdapter keyValueAdapter) {
        return new KeyValueTemplate(keyValueAdapter);
    }

    @Bean
    public HazelcastKeyValueAdapter hazelcastKeyValueAdapter(HazelcastInstance hazelcast) {
        return new HazelcastKeyValueAdapter(hazelcast);
    }
}
