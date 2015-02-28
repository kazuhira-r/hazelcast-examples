package org.littlewings.hazelcast.jcache;

import java.util.concurrent.TimeUnit;

import javax.cache.configuration.Factory;
import javax.cache.expiry.Duration;
import javax.cache.expiry.ExpiryPolicy;
import javax.cache.expiry.TouchedExpiryPolicy;

public class MyExpiryFactory implements Factory<ExpiryPolicy> {
    @Override
    public ExpiryPolicy create() {
        return new TouchedExpiryPolicy(new Duration(TimeUnit.SECONDS, 5));
    }
}
