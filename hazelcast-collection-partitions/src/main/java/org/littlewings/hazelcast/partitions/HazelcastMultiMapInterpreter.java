package org.littlewings.hazelcast.partitions;

import java.util.stream.IntStream;

import com.hazelcast.core.MultiMap;
import com.hazelcast.core.Partition;

public class HazelcastMultiMapInterpreter extends HazelcastInterpreterSupport {
    public static void main(String... args) {
        new HazelcastMultiMapInterpreter().execute(args);
    }

    @Override
    protected void execute(String... args) {
        withHazelcast(hazelcast -> {
            String name = "default";
            MultiMap<String, Integer> map = hazelcast.getMultiMap(name);

            if (args.length > 0) {
                if ("master".equals(args[0])) {
                    IntStream
                            .rangeClosed(1, 10)
                            .forEach(i ->
                                    IntStream.rangeClosed(1, 5).forEach(j -> map.put("key" + i, j)));
                }
            }

            readConsoleWhile(hazelcast,
                    name,
                    () -> {
                        map
                                .keySet()
                                .forEach(k -> {
                                    Partition partition = hazelcast.getPartitionService().getPartition(k);
                                    show("key = %s, values = %s, partitionId = %d, owner = %s.",
                                            k,
                                            map.get(k),
                                            partition.getPartitionId(),
                                            partition.getOwner());
                                });
                        return null;
                    },
                    map::size);
        });
    }
}
