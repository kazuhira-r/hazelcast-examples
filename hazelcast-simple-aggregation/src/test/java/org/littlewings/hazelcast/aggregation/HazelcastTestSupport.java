package org.littlewings.hazelcast.aggregation;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

public interface HazelcastTestSupport {
    default <R> R withHazelcast(int numInstances, Function<HazelcastInstance, R> fun) {
        Config config = new Config();

        List<HazelcastInstance> hazelcastInstances = IntStream
            .rangeClosed(1, numInstances)
            .mapToObj(i -> Hazelcast.newHazelcastInstance(config))
            .collect(Collectors.toList());

        try {
            return hazelcastInstances
                .stream()
                .findFirst()
                .map(hazelcast -> fun.apply(hazelcast))
                .orElse(null);
        } finally {
            hazelcastInstances
                .stream()
                .forEach(h -> h.getLifecycleService().shutdown());

            Hazelcast.shutdownAll();
        }
    }
}
