package org.littlewings.hazelcast.aggregators;

import static org.assertj.core.api.Assertions.*;

import java.util.stream.IntStream;

import com.hazelcast.core.IMap;
import com.hazelcast.mapreduce.aggregation.Aggregations;
import com.hazelcast.mapreduce.aggregation.Supplier;

import org.junit.Test;

public class HazelcastSimpleAggregatorTest implements HazelcastTestSupport {
    @Test
    public void testSimpleAggregator() {
        int result = withHazelcast(1, hazelcast -> {
            IMap<String, Integer> map = hazelcast.getMap("default");

            IntStream.rangeClosed(1, 20).forEach(i -> map.put("key" + i, i));

            return map.aggregate(Supplier.all(), Aggregations.integerSum());
        });

        assertThat(result)
            .isEqualTo(210);
    }

    @Test
    public void testSimpleDoublingAggregator() {
        int result = withHazelcast(2, hazelcast -> {
            IMap<String, Integer> map = hazelcast.getMap("default");

            IntStream.rangeClosed(1, 20).forEach(i -> map.put("key" + i, i));

            return map.aggregate(Supplier.all(value -> value * 2),
                                 Aggregations.integerSum());
        });

        assertThat(result)
            .isEqualTo(420);
    }

    @Test
    public void testEvenAggregator() {
        int result = withHazelcast(1, hazelcast -> {
            IMap<String, Integer> map = hazelcast.getMap("default");

            IntStream.rangeClosed(1, 20).forEach(i -> map.put("key" + i, i));

            return map.aggregate(Supplier.fromPredicate(entry -> entry.getValue()  % 2 == 0),
                                 Aggregations.integerSum());
        });

        assertThat(result)
            .isEqualTo(110);
    }

    @Test
    public void testEvenDoublingAggregator() {
        int result = withHazelcast(1, hazelcast -> {
            IMap<String, Integer> map = hazelcast.getMap("default");

            IntStream.rangeClosed(1, 20).forEach(i -> map.put("key" + i, i));

            return map.aggregate(Supplier.fromPredicate(entry -> entry.getValue()  % 2 == 0,
                                                        Supplier.all(value -> value * 2)),
                                 Aggregations.integerSum());
        });

        assertThat(result)
            .isEqualTo(220);
    }
}
