package org.littlewings.hazelcast.aggregators;

import java.util.Map;

import com.hazelcast.mapreduce.Collator;
import com.hazelcast.mapreduce.CombinerFactory;
import com.hazelcast.mapreduce.Mapper;
import com.hazelcast.mapreduce.ReducerFactory;
import com.hazelcast.mapreduce.aggregation.Aggregation;
import com.hazelcast.mapreduce.aggregation.Supplier;

public class IntegerSumAggregationNoCombiner implements Aggregation<String, Integer, Integer> {
    @Override
    public Collator<Map.Entry, Integer> getCollator() {
        return new IntegerSumCollator();
    }

    @Override
    public CombinerFactory<String, Integer, Integer> getCombinerFactory() {
        return null;
    }

    @Override
    public Mapper<String, Integer, String, Integer> getMapper(Supplier<String, ?, Integer> supplier) {
        return new SupplierDelegateMapper(supplier);
    }

    public ReducerFactory<String, Integer, Integer> getReducerFactory() {
        return new IntegerSumReducerFactory();
    }
}
