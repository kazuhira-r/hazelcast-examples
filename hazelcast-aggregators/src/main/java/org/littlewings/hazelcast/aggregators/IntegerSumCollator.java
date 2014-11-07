package org.littlewings.hazelcast.aggregators;

import java.util.Map;
import java.util.stream.StreamSupport;

import com.hazelcast.mapreduce.Collator;

public class IntegerSumCollator implements Collator<Map.Entry, Integer> {
    public Integer collate(Iterable<Map.Entry> values) {
        return StreamSupport.stream(values.spliterator(), false)
            .mapToInt(entry -> ((Map.Entry<String, Integer>) entry).getValue())
            .sum();
    }
}
