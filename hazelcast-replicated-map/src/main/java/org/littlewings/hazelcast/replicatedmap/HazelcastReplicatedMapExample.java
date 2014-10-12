package org.littlewings.hazelcast.replicatedmap;

import java.time.LocalDateTime;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.hazelcast.config.ClasspathXmlConfig;
import com.hazelcast.config.Config;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.MapEvent;
import com.hazelcast.core.ReplicatedMap;

public class HazelcastReplicatedMapExample {
    public static void main(String[] args) {
        String mapName;
        int entrySize;
        if (args.length > 1) {
            mapName = args[0];
            entrySize = Integer.decode(args[1]);
        } else {
            mapName = "default";
            entrySize = 20;
        }

        Instant start = Instant.now();

        HazelcastInstance hazelcast =
            Hazelcast.newHazelcastInstance(new ClasspathXmlConfig("hazelcast.xml"));

        Map<String, String> map = hazelcast.getReplicatedMap(mapName);
        // Map<String, String> map = hazelcast.getMap(mapName);

        if (map instanceof ReplicatedMap) {
            ReplicatedMap<String, String> rmap = (ReplicatedMap<String, String>)map;
            rmap.addEntryListener(new MyEntryListener());
        } else if (map instanceof IMap) {
            IMap<String, String> rmap = (IMap<String, String>)map;
            rmap.addEntryListener(new MyEntryListener(), true);
        }

        String lastKey = "key" + entrySize;
        System.out.printf("last-key = [%s], value = [%s], exists = [%b]%n",
                          lastKey,
                          map.get("key" + entrySize),
                          map.containsKey("key" + entrySize));

        Instant createdTime = Instant.now();
        Duration timeElapsed = Duration.between(start, createdTime);
        System.out.printf("Cluster Joined Time = [%d] millis%n", timeElapsed.toMillis());

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

    public static class MyEntryListener implements EntryListener<String, String> {
        @Override
        public void entryAdded(EntryEvent<String, String> event) {
            // System.out.println("Entry added: " + event);
        }

        @Override
        public void entryUpdated(EntryEvent<String, String> event) {
            // System.out.println("Entry updated: " + event);
        }

        @Override
        public void entryRemoved(EntryEvent<String, String> event) {
            // System.out.println("Entry removed: " + event);
        }

        @Override
        public void entryEvicted(EntryEvent<String, String> event) {
            // Currently not supported, will never fire
        }

        @Override
        public void mapCleared(MapEvent event) {
        }

        @Override
        public void mapEvicted(MapEvent event) {
        }
    }
}
