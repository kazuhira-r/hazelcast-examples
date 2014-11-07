package org.littlewings.hazelcast.aggregators;

import java.util.Map;

import com.hazelcast.query.Predicate;

public class EvenPredicate implements Predicate<String, Integer> {
    @Override
    public boolean apply(Map.Entry<String, Integer> entry) {
        return entry.getValue() % 2 == 0;
    }
}
