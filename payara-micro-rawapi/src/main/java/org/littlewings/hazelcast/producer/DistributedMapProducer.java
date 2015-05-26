package org.littlewings.hazelcast.producer;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

@Dependent
public class DistributedMapProducer {
    @Inject
    private HazelcastInstance hazelcastInstance;

    @ApplicationScoped
    @Produces
    public IMap<String, String> createSimpleDistributedMap() {
        return hazelcastInstance.getMap("default");
    }

    @ApplicationScoped
    @Produces IMap<String, Integer> createWithExpiryDistributedMap() {
        /*
        Config config = hazelcastInstance.getConfig();

        MapConfig mapConfig = new MapConfig("withExpiryMap");
        mapConfig.setTimeToLiveSeconds(10);

        config.addMapConfig(mapConfig);
        */

        return hazelcastInstance.getMap("withExpiryMap");
    }

    @PostConstruct
    public void configuration() {
        Config config = hazelcastInstance.getConfig();

        MapConfig mapConfig = new MapConfig("withExpiryMap");
        mapConfig.setTimeToLiveSeconds(10);

        config.addMapConfig(mapConfig);
    }
}
