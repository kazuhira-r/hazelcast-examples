package org.littlewings.hazelcast.aggregators;

import java.util.Map;

import com.hazelcast.mapreduce.aggregation.Supplier;

public class DoublingSupplier extends Supplier<String, Integer, Integer> {
    @Override
    public Integer apply(Map.Entry<String, Integer> entry) {
        return entry.getValue() * 2;
    }
}
