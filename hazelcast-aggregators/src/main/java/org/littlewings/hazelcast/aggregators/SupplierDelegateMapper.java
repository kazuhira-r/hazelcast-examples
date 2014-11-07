package org.littlewings.hazelcast.aggregators;

import java.util.AbstractMap;
import java.util.Map;

import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;
import com.hazelcast.mapreduce.aggregation.Supplier;

public class SupplierDelegateMapper implements Mapper<String, Integer, String, Integer> {
    private Supplier<String, ?, Integer> supplier;

    public SupplierDelegateMapper(Supplier<String, ?, Integer> supplier) {
        this.supplier = supplier;
    }

    @Override
    public void map(String key, Integer value, Context<String, Integer> context) {
        AbstractMap.SimpleEntry entry = new AbstractMap.SimpleEntry(key, value);
        Integer valueOut = (Integer) supplier.apply((Map.Entry) entry);

        if (valueOut != null) {
            context.emit(key, valueOut);
        }
    }
}
