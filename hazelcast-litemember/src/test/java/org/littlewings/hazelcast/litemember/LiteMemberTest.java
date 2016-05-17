package org.littlewings.hazelcast.litemember;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.hazelcast.config.ClasspathXmlConfig;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.Partition;
import com.hazelcast.partition.NoDataMemberInClusterException;
import org.assertj.core.data.MapEntry;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class LiteMemberTest {
    @Test
    public void standaloneLiteMember() {
        withLiteMember(hazelcast -> {
            Map<String, String> map = hazelcast.getMap("default");
            assertThatThrownBy(() -> map.put("key", "value"))
                    .isInstanceOf(NoDataMemberInClusterException.class)
                    .hasMessage("Partitions can't be assigned since all nodes in the cluster are lite members");
        });
    }

    @Test
    public void liteMemberWithNormalMember() {
        withHazelcast(hasDataHazelcast -> {
            withLiteMember(liteHazelcast -> {
                Map<String, String> map = liteHazelcast.getMap("default");
                map.put("key", "value");

                assertThat(map)
                        .containsExactly(MapEntry.entry("key", "value"));

                assertThat(liteHazelcast.getConfig().isLiteMember()).isTrue();

                assertThat(liteHazelcast.getCluster().getMembers()).hasSize(2);

                Set<Partition> partitions = liteHazelcast.getPartitionService().getPartitions();
                assertThat(partitions).hasSize(271);
                assertThat(partitions.stream().map(p -> p.getOwner()).distinct().count()).isEqualTo(1);

                Partition partition = partitions.stream().findAny().get();
                assertThat(partition.getOwner())
                        .isEqualTo(hasDataHazelcast.getCluster().getLocalMember())
                        .isNotEqualTo(liteHazelcast.getCluster().getLocalMember());
            });
        });
    }

    @Test
    public void normalHazelcastCluster() {
        withHazelcast(2, hazelcast -> {
            Map<String, String> map = hazelcast.getMap("default");
            map.put("key", "value");

            assertThat(map)
                    .containsExactly(MapEntry.entry("key", "value"));

            assertThat(hazelcast.getConfig().isLiteMember()).isFalse();

            Set<Partition> partitions = hazelcast.getPartitionService().getPartitions();
            assertThat(partitions).hasSize(271);
            assertThat(partitions.stream().map(p -> p.getOwner()).distinct().count()).isEqualTo(2);
        });
    }

    @Test
    public void preConfigurationedInstances() {
        withHazelcast("hazelcast-datamember.xml", 2, hasDataHazelcast -> {
            withHazelcast("hazelcast-litemember.xml", liteHazelcast -> {
                Map<String, String> map = liteHazelcast.getMap("default");
                map.put("key", "value");

                assertThat(map).containsExactly(MapEntry.entry("key", "value"));

                try {
                    TimeUnit.SECONDS.sleep(5L);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                assertThat(map).isEmpty();

                assertThat(liteHazelcast.getCluster().getMembers())
                        .hasSize(3);
                assertThat(liteHazelcast.getConfig().isLiteMember()).isTrue();
                Set<Partition> partitions = liteHazelcast.getPartitionService().getPartitions();
                assertThat(partitions).hasSize(271);
                assertThat(partitions.stream().map(p -> p.getOwner()).distinct().count()).isEqualTo(2);
            });
        });
    }

    protected void withLiteMember(Consumer<HazelcastInstance> consumer) {
        Config config = new Config();
        config.setLiteMember(true);

        HazelcastInstance hazelcast = Hazelcast.newHazelcastInstance(config);
        try {
            consumer.accept(hazelcast);
        } finally {
            hazelcast.getLifecycleService().shutdown();
        }
    }

    protected void withHazelcast(Consumer<HazelcastInstance> consumer) {
        withHazelcast(1, consumer);
    }

    protected void withHazelcast(int numInstances, Consumer<HazelcastInstance> consumer) {
        List<HazelcastInstance> hazelcastInstances = IntStream
                .rangeClosed(1, numInstances)
                .mapToObj(i -> Hazelcast.newHazelcastInstance(new Config()))
                .collect(Collectors.toList());

        hazelcastInstances.forEach(h -> h.getLifecycleService().shutdown());
    }

    protected void withHazelcast(String configFilePath, Consumer<HazelcastInstance> consumer) {
        withHazelcast(configFilePath, 1, consumer);
    }

    protected void withHazelcast(String configFilePath, int numInstances, Consumer<HazelcastInstance> consumer) {
        List<HazelcastInstance> hazelcastInstances = IntStream
                .rangeClosed(1, numInstances)
                .mapToObj(i -> {
                    ClasspathXmlConfig config = new ClasspathXmlConfig(configFilePath);
                    return Hazelcast.newHazelcastInstance(config);
                })
                .collect(Collectors.toList());

        hazelcastInstances.forEach(h -> h.getLifecycleService().shutdown());
    }
}
