package org.littlewings.hazelcast.jcache;

import java.util.stream.IntStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.spi.CachingProvider;

public class WaitServer {
    public static void main(String[] args) {
        try (CachingProvider provider = Caching.getCachingProvider();
             CacheManager manager = provider.getCacheManager();
             Cache<String, Integer> cache = manager.createCache("entryProcessorCache",
                                                                new MutableConfiguration<>())) {
            IntStream
                .rangeClosed(1, 20)
                .forEach(i -> cache.put("key" + i, i));

            LocalDateTime now  = LocalDateTime.now();

            System.out.printf("[%s] Wait Cache Server startup.", now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            System.console().readLine();
        }
    }
}
