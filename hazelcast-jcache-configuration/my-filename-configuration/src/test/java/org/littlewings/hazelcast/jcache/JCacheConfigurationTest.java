package org.littlewings.hazelcast.jcache;

import static org.assertj.core.api.Assertions.*;

import java.net.URI;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;

import com.hazelcast.cache.HazelcastCachingProvider;

import org.junit.Test;

public class JCacheConfigurationTest {
    @Test
    public void testSimpleJCache() {
        Properties properties = new Properties();
        properties.setProperty(HazelcastCachingProvider.HAZELCAST_CONFIG_LOCATION,
                               "classpath:my-hazelcast.xml");

        CachingProvider provider = Caching.getCachingProvider();
        CacheManager manager = provider.getCacheManager(URI.create("my-cache-manager"),
                                                        null,
                                                        properties);
        Cache<String, String> cache = manager.getCache("simple-cache",
                                                       String.class,
                                                       String.class);

        IntStream.rangeClosed(1, 5).forEach(i -> cache.put("key" + i, "value" + i));

        assertThat(cache.get("key1"))
            .isEqualTo("value1");
        assertThat(cache.get("key5"))
            .isEqualTo("value5");
        assertThat(cache.get("key10"))
            .isNull();

        cache.close();
        manager.close();
        provider.close();
    }

    @Test
    public void testEviction() {
        Properties properties =
            HazelcastCachingProvider.propertiesByLocation("classpath:my-hazelcast.xml");

        CachingProvider provider = Caching.getCachingProvider();
        CacheManager manager = provider.getCacheManager(URI.create("my-cache-manager"),
                                                        null,
                                                        properties);

        Cache<String, String> cache = manager.getCache("with-eviction-cache",
                                                       String.class,
                                                       String.class);

        com.hazelcast.cache.ICache<String, String> icache =
            cache.unwrap(com.hazelcast.cache.ICache.class);
        com.hazelcast.config.CacheConfig config =
            icache.getConfiguration(com.hazelcast.config.CacheConfig.class);

        assertThat(config.getEvictionConfig().getSize())
            .isEqualTo(5);  // right

        IntStream.rangeClosed(1, 30).forEach(i -> cache.put("key" + i, "value" + i));

        int count = 0;
        for (Cache.Entry<String, String> entry : cache) {
            count++;
        }

        assertThat(count)
            .isGreaterThan(25);  // much larger than ENTRY_COUNT??

        cache.close();
        manager.close();
        provider.close();
    }

    @Test
    public void testExpire() throws InterruptedException {
        Properties properties =
            HazelcastCachingProvider.propertiesByLocation("classpath:my-hazelcast.xml");

        CachingProvider provider = Caching.getCachingProvider();
        CacheManager manager = provider.getCacheManager(URI.create("my-cache-manager"),
                                                        null,
                                                        properties);

        Cache<String, String> cache = manager.getCache("with-expire-cache",
                                                       String.class,
                                                       String.class);

        IntStream.rangeClosed(1, 5).forEach(i -> cache.put("key" + i, "value" + i));

        TimeUnit.SECONDS.sleep(3);

        cache.get("key1");
        cache.get("key3");

        TimeUnit.SECONDS.sleep(3);

        assertThat(cache.get("key1"))
            .isEqualTo("value1");
        assertThat(cache.get("key3"))
            .isEqualTo("value3");
        assertThat(cache.get("key2"))
            .isNull();
        assertThat(cache.get("key4"))
            .isNull();

        TimeUnit.SECONDS.sleep(6);

        assertThat(cache.get("key1"))
            .isNull();
        assertThat(cache.get("key3"))
            .isNull();

        cache.close();
        manager.close();
        provider.close();
    }

    @Test
    public void testEvictionWithExpire() throws InterruptedException {
        Properties properties =
            HazelcastCachingProvider.propertiesByLocation("classpath:my-hazelcast.xml");

        CachingProvider provider = Caching.getCachingProvider();
        CacheManager manager = provider.getCacheManager(URI.create("my-cache-manager"),
                                                        null,
                                                        properties);
        Cache<String, String> cache = manager.getCache("with-expire-cache",
                                                       String.class,
                                                       String.class);

        IntStream.rangeClosed(1, 30).forEach(i -> cache.put("key" + i, "value" + i));

        TimeUnit.SECONDS.sleep(3);

        cache.get("key1");
        cache.get("key3");
        cache.get("key5");
        cache.get("key10");
        cache.get("key13");
        cache.get("key15");
        cache.get("key20");
        cache.get("key25");

        TimeUnit.SECONDS.sleep(3);

        int count = 0;
        for (Cache.Entry<String, String> entry : cache) {
            count++;
        }

        assertThat(count)
            .isLessThanOrEqualTo(8);

        cache.close();
        manager.close();
        provider.close();
    }
}
