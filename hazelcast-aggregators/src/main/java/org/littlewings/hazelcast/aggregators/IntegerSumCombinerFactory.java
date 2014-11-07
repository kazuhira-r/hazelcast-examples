package org.littlewings.hazelcast.aggregators;

import com.hazelcast.mapreduce.Combiner;
import com.hazelcast.mapreduce.CombinerFactory;

public class IntegerSumCombinerFactory implements CombinerFactory<String, Integer, Integer> {
    @Override
    public Combiner<Integer, Integer> newCombiner(String key) {
        return new IntegerSumCombiner();
    }

    private static class IntegerSumCombiner extends Combiner<Integer, Integer> {
        private int sum;

        @Override
        public void combine(Integer value) {
            sum += value;
        }

        @Override
        public Integer finalizeChunk() {
            return sum;
        }

        @Override
        public void reset() {
            sum = 0;
        }
    }
}
