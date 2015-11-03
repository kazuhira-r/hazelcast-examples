package org.littlewings.hazelcast.partitions;

import java.util.stream.IntStream;

import com.hazelcast.core.IMap;
import com.hazelcast.core.Partition;

public class HazelcastMapInterpreter extends HazelcastInterpreterSupport {
    public static void main(String... args) {
        new HazelcastMapInterpreter().execute(args);
    }

    @Override
    protected void execute(String... args) {
        withHazelcast(hazelcast -> {
            String name = "default";
            IMap<String, Integer> map = hazelcast.getMap(name);

            if (args.length > 0) {
                if ("master".equals(args[0])) {
                    IntStream.rangeClosed(1, 10).forEach(i -> map.put("key" + i, i));
                }
            }

            readConsoleWhile(hazelcast,
                    name,
                    () -> {
                        map
                                .keySet()
                                .forEach(k -> {
                                    Partition partition = hazelcast.getPartitionService().getPartition(k);
                                    show("key = %s, partitionId = %d, owner = %s.", k, partition.getPartitionId(), partition.getOwner());
                                });
                        return null;
                    },
                    map::size);
        });
    }
}
