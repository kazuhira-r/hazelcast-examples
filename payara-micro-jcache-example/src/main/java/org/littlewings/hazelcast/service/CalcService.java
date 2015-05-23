package org.littlewings.hazelcast.service;

import java.util.concurrent.TimeUnit;
import javax.cache.annotation.CacheDefaults;
import javax.cache.annotation.CacheResult;
import javax.enterprise.context.ApplicationScoped;

@CacheDefaults(cacheName = "calcCache")
@ApplicationScoped
public class CalcService {
    @CacheResult
    public int add(int a, int b) {
        try {
            TimeUnit.SECONDS.sleep(3L);
        } catch (InterruptedException e) {
        }

        return a + b;
    }
}
