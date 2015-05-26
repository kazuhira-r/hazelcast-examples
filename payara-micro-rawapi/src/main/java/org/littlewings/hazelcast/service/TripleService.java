package org.littlewings.hazelcast.service;

import java.util.concurrent.TimeUnit;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.hazelcast.core.IMap;

@ApplicationScoped
public class TripleService {
    @Inject
    private IMap<String, Integer> withExpiryMap;

    public int execute(String key, int seed) {
        if (withExpiryMap.containsKey(key)) {
            return withExpiryMap.get(key);
        }

        try {
            TimeUnit.SECONDS.sleep(3L);
        } catch (InterruptedException e) {
        }

        int tripled = seed * 3;

        withExpiryMap.put(key, tripled);
        return withExpiryMap.get(key);
    }
}
