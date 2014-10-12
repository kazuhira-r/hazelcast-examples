package org.littlewings.hazelcast.replicatedmap;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

public class ReplicatedMapExample {
    public static void main(String[] args) {
        Config config = new Config();
        HazelcastInstance hazelcast =
            Hazelcast.newHazelcastInstance(config);

        Map<String, String> map = hazelcast.getReplicatedMap("default");
        // Map<String, String> map = hazelcast.getMap("default");

        int entrySize = 20;

        IntStream
            .rangeClosed(1, entrySize)
            .forEach(i -> map.put("key" + i, "value" + i));

        System.out.printf("[%s] Hazelcast Node, startup, putted [%d]entries.%n",
                          LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), entrySize);
        System.console().readLine("Enter, shutdown...");

        IntStream
            .rangeClosed(1, entrySize)
            .forEach(i -> {
                String key = "key" + i;
                String value = map.get(key);
                System.out.printf("Key = [%s], Value = [%s]%n", key, value);
            });

        hazelcast.getLifecycleService();
        Hazelcast.shutdownAll();
    }
}
