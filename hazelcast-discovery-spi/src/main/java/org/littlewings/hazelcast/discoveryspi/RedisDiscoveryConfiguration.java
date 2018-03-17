package org.littlewings.hazelcast.discoveryspi;

import com.hazelcast.config.properties.PropertyDefinition;
import com.hazelcast.config.properties.PropertyTypeConverter;
import com.hazelcast.config.properties.SimplePropertyDefinition;

public interface RedisDiscoveryConfiguration {
    PropertyDefinition REDIS_URL =
            new SimplePropertyDefinition("redis-url", true, PropertyTypeConverter.STRING);
}
