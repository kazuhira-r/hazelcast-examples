package org.littlewings.hazelcast.aggregation;

import static org.assertj.core.api.Assertions.*;

import java.io.Serializable;
import java.util.Map;
import java.util.stream.IntStream;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.mapreduce.aggregation.Aggregations;
import com.hazelcast.mapreduce.aggregation.Supplier;

import org.junit.Test;

public class SimpleAggregationTest implements HazelcastTestSupport {
    @Test
    public void testSimpleAggregation() {
        int result = withHazelcast(3, hazelcast -> {
            IMap<String, Integer> map = hazelcast.getMap("default");

            IntStream
                .rangeClosed(1, 20)
                .forEach(i -> map.put("key" + i, i));

            return map.aggregate(Supplier.all(), Aggregations.integerSum());
        });

        assertThat(result)
            .isEqualTo(210);
    }

    @Test
    public void testDoublingAggregation() {
        int result = withHazelcast(3, hazelcast -> {
            IMap<String, Integer> map = hazelcast.getMap("default");

            IntStream
                .rangeClosed(1, 20)
                .forEach(i -> map.put("key" + i, i));

            return map.aggregate(new DoublingSupplier(), Aggregations.integerSum());
        });

        assertThat(result)
            .isEqualTo(420);
    }

    public static class DoublingSupplier extends Supplier<String, Integer, Integer> implements Serializable {
        @Override
        public Integer apply(Map.Entry<String, Integer> entry) {
            return entry.getValue() * 2;
        }
    }
}
