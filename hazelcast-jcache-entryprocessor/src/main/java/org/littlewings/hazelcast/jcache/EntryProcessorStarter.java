package org.littlewings.hazelcast.jcache;

import java.io.Serializable;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.processor.EntryProcessor;
import javax.cache.processor.EntryProcessorResult;
import javax.cache.processor.MutableEntry;
import javax.cache.spi.CachingProvider;

public class EntryProcessorStarter {
    public static void main(String[] args) {
        try (CachingProvider provider = Caching.getCachingProvider();
             CacheManager manager = provider.getCacheManager();
             Cache<String, Integer> cache = manager.createCache("entryProcessorCache",
                                                                new MutableConfiguration<>())) {
            /*
            IntStream
                .rangeClosed(1, 20)
                .forEach(i -> cache.put("key" + i, i));
            */

            Set<String> keys = new HashSet<>();
            cache.forEach(entry -> keys.add(entry.getKey()));

            /*
            Map<String, EntryProcessorResult<Integer>> results =
                cache.invokeAll(keys, (entry, arguments) -> {
                    System.out.printf("[%s] key = %s%n", Thread.currentThread().getName(), entry.getKey());

                    if (entry.exists()) {
                        return entry.getValue() * 2;
                    } else {
                        return 0;
                    }
                });
            */

            Map<String, EntryProcessorResult<Integer>> results =
                cache.invokeAll(keys, new MyEntryProcessor());

            int result = results.entrySet()
                .stream()
                .mapToInt(e -> e.getValue().get())
                .sum();
            
            System.out.printf("Result = %d%n", result);
        }
    }

    public static class MyEntryProcessor implements EntryProcessor<String, Integer, Integer>, Serializable {
        @Override
        public Integer process(MutableEntry<String, Integer> entry, Object... arguments) {
            System.out.printf("[%s] key = %s%n", Thread.currentThread().getName(), entry.getKey());

            if (entry.exists()) {
                return entry.getValue() * 2;
            } else {
                return 0;
            }
        }
    }
}
