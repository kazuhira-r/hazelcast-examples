package org.littlewings.hazelcast.aggregators;

import static org.assertj.core.api.Assertions.*;

import java.util.stream.IntStream;

import com.hazelcast.core.IMap;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.mapreduce.aggregation.Supplier;

import org.junit.Test;

public class MyAggregationTest implements HazelcastTestSupport {
    @Test
    public void testMyAggregation() {
        int result = withHazelcast(2, "hazelcast-aggregations.xml", hazelcast -> {
            assertThat(hazelcast.getConfig().getMapConfig("aggregateMap").getBackupCount())
                .isEqualTo(1);
            assertThat(hazelcast.getConfig().getJobTrackerConfig("aggregateJobTracker").getChunkSize())
                .isEqualTo(500);

            IMap<String, Integer> map = hazelcast.getMap("aggregateMap");
            IntStream.rangeClosed(1, 20).forEach(i -> map.put("key" + i, i));

            JobTracker jobTracker = hazelcast.getJobTracker("aggregateJobTracker");

            return map.aggregate(Supplier.fromPredicate(new EvenPredicate(), new DoublingSupplier()),
                                 new IntegerSumAggregation(),
                                 jobTracker);
        });

        assertThat(result)
            .isEqualTo(220);
    }

    @Test
    public void testMyAggregationNoCombiner() {
        int result = withHazelcast(2, "hazelcast-aggregations.xml", hazelcast -> {
            assertThat(hazelcast.getConfig().getMapConfig("aggregateMap").getBackupCount())
                .isEqualTo(1);
            assertThat(hazelcast.getConfig().getJobTrackerConfig("aggregateJobTracker").getChunkSize())
                .isEqualTo(500);

            IMap<String, Integer> map = hazelcast.getMap("aggregateMap");
            IntStream.rangeClosed(1, 20).forEach(i -> map.put("key" + i, i));

            JobTracker jobTracker = hazelcast.getJobTracker("aggregateJobTracker");

            return map.aggregate(Supplier.fromPredicate(new EvenPredicate(), new DoublingSupplier()),
                                 new IntegerSumAggregationNoCombiner(),
                                 jobTracker);
        });

        assertThat(result)
            .isEqualTo(220);
    }
}
