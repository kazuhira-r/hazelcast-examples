package org.littlewings.hazelcast.distexec;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.scheduledexecutor.IScheduledExecutorService;
import com.hazelcast.scheduledexecutor.IScheduledFuture;

public class SingleMemberCallableTaskExecutor {
    public static void main(String... args) throws ExecutionException, InterruptedException {
        HazelcastInstance hazelcast = Hazelcast.newHazelcastInstance();

        try {
            IScheduledExecutorService es = hazelcast.getScheduledExecutorService("default");
            IScheduledFuture<String> future = es.schedule(new HelloCallableTask(), 5, TimeUnit.SECONDS);
            System.out.println(future.get());
        } finally {
            hazelcast.shutdown();
            Hazelcast.shutdownAll();
        }
    }
}
