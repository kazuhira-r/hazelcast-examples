package org.littlewings.hazelcast.aggregators;

import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

public class IntegerSumReducerFactory implements ReducerFactory<String, Integer, Integer> {
    @Override
    public Reducer<Integer, Integer> newReducer(String key) {
        return new IntegerSumReducer();
    }

    private static class IntegerSumReducer extends Reducer<Integer, Integer> {
        private int sum;

        @Override
        public void reduce(Integer value) {
            sum += value;
        }

        @Override
        public Integer finalizeReduce() {
            return sum;
        }
    }
}
