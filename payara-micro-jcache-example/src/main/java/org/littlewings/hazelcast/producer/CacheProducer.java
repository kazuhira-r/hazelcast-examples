package org.littlewings.hazelcast.producer;

import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.cache.CacheManager;
import javax.cache.configuration.Configuration;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

@Singleton
@Startup
public class CacheProducer {
    @Inject
    private CacheManager manager;

    @PostConstruct
    public void createCalcCache() {
        Configuration<?, ?> configuration =
                new MutableConfiguration<>()
                        .setExpiryPolicyFactory(
                                CreatedExpiryPolicy
                                        .factoryOf(new Duration(TimeUnit.SECONDS, 10)));

        if (manager.getCache("calcCache") == null) {
            manager.createCache("calcCache", configuration);
        }
    }
}
