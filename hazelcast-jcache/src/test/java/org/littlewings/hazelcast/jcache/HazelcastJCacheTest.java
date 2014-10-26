package org.littlewings.hazelcast.jcache;

import static org.assertj.core.api.Assertions.*;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.Configuration;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.AccessedExpiryPolicy;
import javax.cache.expiry.Duration;
import javax.cache.spi.CachingProvider;

import org.junit.Ignore;
import org.junit.Test;

public class HazelcastJCacheTest {
    @Test
    public void testJCacheGettingStarted() {
        try (CachingProvider provider = Caching.getCachingProvider();
             CacheManager manager = provider.getCacheManager();
             Cache<String, String> cache = manager.createCache("testCache",
                                                               new MutableConfiguration<>())) {
            cache.put("key", "value");
            assertThat(cache.get("key")).isEqualTo("value");

            cache.remove("key");
            assertThat(cache.get("key")).isNull();
        }
    }

    @Test
    public void testCacheExpired() throws Exception {
        Configuration<String, String> configuration =
            new MutableConfiguration<String, String>()
              .setExpiryPolicyFactory(AccessedExpiryPolicy.factoryOf(new Duration(TimeUnit.SECONDS, 3)));

        try (CachingProvider provider = Caching.getCachingProvider();
             CacheManager manager = provider.getCacheManager();
             Cache<String, String> cache = manager.createCache("testCache",
                                                               configuration)) {
            cache.put("key1", "value1");
            cache.put("key2", "value2");

            TimeUnit.SECONDS.sleep(1);

            cache.get("key2");

            TimeUnit.SECONDS.sleep(2);

            assertThat(cache.get("key1")).isNull();
            assertThat(cache.get("key2")).isEqualTo("value2");
        }
    }

    @Ignore
    @Test
    public void testWithHazelcastConfiguration() throws Exception {
        // wrong use!!

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URI uri = classLoader.getResource("hazelcast-jcache.xml").toURI();

        try (CachingProvider provider = Caching.getCachingProvider();
             CacheManager manager = provider.getCacheManager(uri, classLoader);
             Cache<String, String> cache = manager.getCache("testCache")) {
            cache.put("key1", "value1");
            cache.put("key2", "value2");

            TimeUnit.SECONDS.sleep(1);

            cache.get("key2");

            TimeUnit.SECONDS.sleep(2);

            assertThat(cache.get("key1")).isNull();
            assertThat(cache.get("key2")).isEqualTo("value2");
        }
    }
}
