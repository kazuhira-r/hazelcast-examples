package org.littlewings.hazelcast;

import java.util.concurrent.TimeUnit;
import javax.cache.expiry.Duration;
import javax.cache.expiry.ExpiryPolicy;

public class MyExpiryPolicy implements ExpiryPolicy {
    @Override
    public Duration getExpiryForCreation() {
        return new Duration(TimeUnit.SECONDS, 10);
    }

    @Override
    public Duration getExpiryForAccess() {
        return null;
    }

    @Override
    public Duration getExpiryForUpdate() {
        return null;
    }
}
