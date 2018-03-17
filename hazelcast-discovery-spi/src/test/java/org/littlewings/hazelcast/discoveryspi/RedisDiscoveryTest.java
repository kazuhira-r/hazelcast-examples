package org.littlewings.hazelcast.discoveryspi;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RedisDiscoveryTest {
    @Test
    public void redisDiscoveryCluster() {
        List<HazelcastInstance> hazelcasts =
                IntStream
                        .rangeClosed(1, 3)
                        .mapToObj(i -> Hazelcast.newHazelcastInstance())
                        .collect(Collectors.toList());

        HazelcastInstance hazelcast = hazelcasts.get(0);

        // クラスタが構成できている
        assertThat(hazelcast.getCluster().getMembers()).hasSize(3);

        hazelcasts.forEach(HazelcastInstance::shutdown);
        Hazelcast.shutdownAll();
    }

    @Test
    public void nodeUpDown() throws InterruptedException {
        List<HazelcastInstance> hazelcasts =
                new ArrayList<>(
                        IntStream
                                .rangeClosed(1, 3)
                                .mapToObj(i -> Hazelcast.newHazelcastInstance())
                                .collect(Collectors.toList())
                );

        HazelcastInstance hazelcast = hazelcasts.get(0);

        assertThat(hazelcast.getCluster().getMembers()).hasSize(3);

        // ひとつNode Down
        HazelcastInstance lastInstance = hazelcasts.get(2);
        lastInstance.shutdown();
        hazelcasts.remove(lastInstance);

        TimeUnit.SECONDS.sleep(3L);

        // Nodeが減っている
        assertThat(hazelcast.getCluster().getMembers()).hasSize(2);

        // Nodeを3つ追加
        hazelcasts.add(Hazelcast.newHazelcastInstance());
        hazelcasts.add(Hazelcast.newHazelcastInstance());
        hazelcasts.add(Hazelcast.newHazelcastInstance());

        // Nodeが追加されている
        assertThat(hazelcast.getCluster().getMembers()).hasSize(5);

        hazelcasts.forEach(HazelcastInstance::shutdown);
        Hazelcast.shutdownAll();
    }
}
