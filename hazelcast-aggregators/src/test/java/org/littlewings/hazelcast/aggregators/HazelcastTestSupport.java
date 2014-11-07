package org.littlewings.hazelcast.aggregators;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.hazelcast.config.ClasspathXmlConfig;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

interface HazelcastTestSupport {
    default <R> R withHazelcast(int numInstances, String xmlConfigFileName, Function<HazelcastInstance, R> fun) {
        return withHazelcast(numInstances, new ClasspathXmlConfig(xmlConfigFileName), fun);
    }

    default <R> R withHazelcast(int numInstances, Function<HazelcastInstance, R> fun) {
        return withHazelcast(numInstances, (Config) null, fun);
    }

    default <R> R withHazelcast(int numInstances, Config config, Function<HazelcastInstance, R> fun) {
        Config c;
        if (config == null) {
            c = new Config();
        } else {
            c = config;
        }

        List<HazelcastInstance> hazelcastInstances =
            IntStream
                .rangeClosed(1, numInstances)
                .mapToObj(i -> Hazelcast.newHazelcastInstance(c))
                .collect(Collectors.toList());

        try {
            return fun.apply(hazelcastInstances.get(0));
        } finally {
            hazelcastInstances
                .stream()
                .forEach(h -> h.getLifecycleService().shutdown());

            Hazelcast.shutdownAll();
        }
    }
}
