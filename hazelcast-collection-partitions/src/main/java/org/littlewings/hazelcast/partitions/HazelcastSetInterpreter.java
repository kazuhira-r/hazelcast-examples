package org.littlewings.hazelcast.partitions;

import java.util.stream.IntStream;

import com.hazelcast.core.ISet;

public class HazelcastSetInterpreter extends HazelcastInterpreterSupport {
    public static void main(String... args) {
        new HazelcastSetInterpreter().execute(args);
    }

    @Override
    protected void execute(String... args) {
        withHazelcast(hazelcast -> {
            String name = "default";
            ISet<Integer> set = hazelcast.getSet(name);

            if (args.length > 0) {
                if ("master".equals(args[0])) {
                    IntStream.rangeClosed(1, 10).forEach(set::add);
                }
            }

            readConsoleWhile(hazelcast,
                    name,
                    () -> {
                        set.forEach(e -> show("element = %d.", e));
                        return null;
                    },
                    set::size);
        });
    }
}
