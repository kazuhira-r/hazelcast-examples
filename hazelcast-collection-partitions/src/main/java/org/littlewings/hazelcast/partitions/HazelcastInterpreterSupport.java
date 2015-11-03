package org.littlewings.hazelcast.partitions;

import java.io.Console;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.Partition;
import com.hazelcast.core.PartitionService;
import com.hazelcast.instance.HazelcastInstanceProxy;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.SerializationService;
import com.hazelcast.partition.strategy.StringPartitioningStrategy;

public abstract class HazelcastInterpreterSupport {
    protected void withHazelcast(Consumer<HazelcastInstance> consumer) {
        HazelcastInstance hazelcast = Hazelcast.newHazelcastInstance();

        try {
            logging("Hazelcast startup.");
            consumer.accept(hazelcast);
        } finally {
            hazelcast.getLifecycleService().shutdown();
            Hazelcast.shutdownAll();
            logging("Hazelcast shutdown.");
        }
    }

    protected abstract void execute(String... args);

    protected void readConsoleWhile(HazelcastInstance hazelcast, String name, Supplier<Void> showAll, Supplier<Integer> counter) {
        Console console = System.console();
        String line;
        while ((line = console.readLine("> ")) != null) {
            if (line.isEmpty()) {
                continue;
            }

            String[] tokens = line.split("\\s+", -1);
            String command = tokens[0];
            boolean stop = false;

            switch (command) {
                case "all":
                    showAll.get();
                    break;
                case "locate":
                    if (tokens.length > 1) {
                        String key = tokens[1];
                        Partition partition = hazelcast.getPartitionService().getPartition(key);
                        show("Locate key = %s, partitionId = %s, owner = %s.", key, partition.getPartitionId(), partition.getOwner());
                    } else {
                        show("Locate, required key.");
                    }
                    break;
                case "self":
                    show("Self = %s.", hazelcast.getCluster().getLocalMember());
                    break;
                case "name":
                    PartitionService partitionService = hazelcast.getPartitionService();
                    SerializationService serializationService = ((HazelcastInstanceProxy)hazelcast).getSerializationService();
                    Data key = serializationService.toData(name, StringPartitioningStrategy.INSTANCE);
                    Partition partition = partitionService.getPartition(key);
                    show("Partition by name = %s, partitionId = %s, owner = %s.", name, partition.getPartitionId(), partition.getOwner());
                    break;
                case "size":
                    show("This data set size = %d.", counter.get());
                    break;
                case "exit":
                    stop = true;
                    break;
                default:
                    show("Unknown command = %s.", command);
                    break;
            }

            if (stop) {
                break;
            }
        }
    }

    protected void logging(String format, Object... args) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
        System.out.println("[" + formatter.format(now) + "] " + String.format(format, args));
    }

    protected void show(String format, Object... args) {
        System.out.println(String.format(format, args));
    }
}
