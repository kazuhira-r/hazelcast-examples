package org.llittlewings.hazelcast.indexing;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.hazelcast.config.ClasspathXmlConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

public abstract class HazelcastTestSupport {
    protected void withHazelcast(int numInstances, Consumer<HazelcastInstance> f) {
        int initialPort = 5701;
        List<HazelcastInstance> instances =
                IntStream
                        .rangeClosed(1, numInstances)
                        .mapToObj(i -> {
                            ClasspathXmlConfig config = new ClasspathXmlConfig("hazelcast.xml");
                            config.setInstanceName("MyHazelcastInstance-" + (initialPort + i - 1));
                            return Hazelcast.newHazelcastInstance(config);
                        })
                        .collect(Collectors.toList());

        try {
            f.accept(instances.get(0));
        } finally {
            instances
                    .stream()
                    .forEach(h -> h.getLifecycleService().shutdown());
            Hazelcast.shutdownAll();
        }
    }
}
