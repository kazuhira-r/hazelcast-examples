package org.littlewings.hazelcast.spring;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.hazelcast.HazelcastKeyValueAdapter;
import org.springframework.data.hazelcast.repository.config.Constants;
import org.springframework.data.hazelcast.repository.config.EnableHazelcastRepositories;
import org.springframework.data.keyvalue.core.KeyValueOperations;
import org.springframework.data.keyvalue.core.KeyValueTemplate;

@Configuration
@EnableHazelcastRepositories
public class HazelcastConfig {
    /*
    @Bean(destroyMethod = "shutdown")
    public HazelcastInstance hazelcastInstance() {
        Config config = new Config();
        config.setInstanceName(Constants.HAZELCAST_INSTANCE_NAME);
        return Hazelcast.newHazelcastInstance(config);
    }
    */

    @Bean
    public KeyValueOperations keyValueTemplate(HazelcastKeyValueAdapter keyValueAdapter) {
        return new KeyValueTemplate(keyValueAdapter);
    }

    @Bean
    public HazelcastKeyValueAdapter hazelcastKeyValueAdapter() {
        return new HazelcastKeyValueAdapter();
    }

    /*
    @Bean
    public HazelcastKeyValueAdapter hazelcastKeyValueAdapter(HazelcastInstance hazelcast) {
        return new HazelcastKeyValueAdapter(hazelcast);
    }
    */
}
