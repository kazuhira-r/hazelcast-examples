package org.littlewings.hazelcast.partitions;

import java.util.stream.IntStream;

import com.hazelcast.core.IList;
import com.hazelcast.core.IQueue;

public class HazelcastQueueInterpreter extends HazelcastInterpreterSupport {
    public static void main(String... args) {
        new HazelcastQueueInterpreter().execute(args);
    }

    @Override
    protected void execute(String... args) {
        withHazelcast(hazelcast -> {
            String name = "default";
            IQueue<Integer> queue = hazelcast.getQueue(name);

            if (args.length > 0) {
                if ("master".equals(args[0])) {
                    IntStream.rangeClosed(1, 10).forEach(i -> {
                        try {
                            queue.put(i);
                        } catch (InterruptedException e) {

                        }
                    });
                }
            }

            readConsoleWhile(hazelcast,
                    name,
                    () -> {
                        queue.forEach(e -> show("element = %d.", e));
                        return null;
                    },
                    queue::size);
        });
    }
}
