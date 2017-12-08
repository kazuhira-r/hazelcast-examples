package org.littlewings.hazelcast.distexec;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.scheduledexecutor.IScheduledExecutorService;
import com.hazelcast.scheduledexecutor.IScheduledFuture;

public class SingleMemberTaskExecutor {
    public static void main(String... args) throws ExecutionException, InterruptedException {
        HazelcastInstance hazelcast = Hazelcast.newHazelcastInstance();

        try {
            IScheduledExecutorService es = hazelcast.getScheduledExecutorService("default");
            IScheduledFuture<?> future = es.schedule(new SayHelloTask(), 5, TimeUnit.SECONDS);
            future.get();
        } finally {
            hazelcast.shutdown();
            Hazelcast.shutdownAll();
        }
    }
}
