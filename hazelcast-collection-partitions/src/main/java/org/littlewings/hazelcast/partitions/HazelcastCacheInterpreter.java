package org.littlewings.hazelcast.partitions;

import java.util.stream.IntStream;
import java.util.stream.StreamSupport;
import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.Configuration;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.spi.CachingProvider;

import com.hazelcast.cache.HazelcastCacheManager;
import com.hazelcast.cache.ICache;
import com.hazelcast.cache.impl.HazelcastServerCacheManager;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.Partition;

public class HazelcastCacheInterpreter extends HazelcastInterpreterSupport {
    public static void main(String... args) {
        new HazelcastCacheInterpreter().execute(args);
    }

    @Override
    protected void execute(String... args) {
        String name = "default";

        Configuration<String, Integer> configuration =
                new MutableConfiguration<String, Integer>()
                        .setTypes(String.class, Integer.class);

        try (CachingProvider cachingProvider = Caching.getCachingProvider();
             CacheManager cacheManager = cachingProvider.getCacheManager();
             Cache<String, Integer> cache = cacheManager.createCache(name, configuration)) {
            HazelcastCacheManager hazelcastCacheManager = cacheManager.unwrap(HazelcastServerCacheManager.class);
            HazelcastInstance hazelcast = hazelcastCacheManager.getHazelcastInstance();
            ICache<String, Integer> hazelcastCache = cache.unwrap(ICache.class);

            if (args.length > 0) {
                if ("master".equals(args[0])) {
                    IntStream.rangeClosed(1, 10).forEach(i -> hazelcastCache.put("key" + i, i));
                }
            }

            readConsoleWhile(hazelcast,
                    name,
                    () -> {
                        StreamSupport.stream(hazelcastCache.spliterator(), false)
                                .forEach(entry -> {
                                    String k = entry.getKey();
                                    Partition partition = hazelcast.getPartitionService().getPartition(k);
                                    show("key = %s, partitionId = %d, owner = %s.", k, partition.getPartitionId(), partition.getOwner());
                                });
                        return null;
                    },
                    hazelcastCache::size);
        }
    }
}
