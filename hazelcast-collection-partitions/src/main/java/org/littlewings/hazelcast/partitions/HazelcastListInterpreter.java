package org.littlewings.hazelcast.partitions;

import java.util.stream.IntStream;

import com.hazelcast.core.IList;

public class HazelcastListInterpreter extends HazelcastInterpreterSupport {
    public static void main(String... args) {
        new HazelcastListInterpreter().execute(args);
    }

    @Override
    protected void execute(String... args) {
        withHazelcast(hazelcast -> {
            String name = "default";
            IList<Integer> list = hazelcast.getList(name);

            if (args.length > 0) {
                if ("master".equals(args[0])) {
                    IntStream.rangeClosed(1, 10).forEach(list::add);
                }
            }

            readConsoleWhile(hazelcast,
                    name,
                    () -> {
                        list.forEach(e -> show("element = %d.", e));
                        return null;
                    },
                    list::size);
        });
    }
}
