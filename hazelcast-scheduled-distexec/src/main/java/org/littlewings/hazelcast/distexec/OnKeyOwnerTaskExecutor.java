package org.littlewings.hazelcast.distexec;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.scheduledexecutor.IScheduledExecutorService;
import com.hazelcast.scheduledexecutor.IScheduledFuture;

public class OnKeyOwnerTaskExecutor {
    public static void main(String... args) throws ExecutionException, InterruptedException {
        HazelcastInstance hazelcast = Hazelcast.newHazelcastInstance();

        try {
            String key = "key1";

            IScheduledExecutorService es = hazelcast.getScheduledExecutorService("default");
            IScheduledFuture<?> future = es.scheduleOnKeyOwner(new HelloRunnableTask(), key, 5, TimeUnit.SECONDS);
            future.get();
        } finally {
            hazelcast.shutdown();
            Hazelcast.shutdownAll();
        }
    }
}
