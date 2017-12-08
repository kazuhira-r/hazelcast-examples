package org.littlewings.hazelcast.distexec;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.hazelcast.config.ScheduledExecutorConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.scheduledexecutor.IScheduledExecutorService;
import com.hazelcast.scheduledexecutor.IScheduledFuture;

public class ConfiguredExecutorServiceRunner {
    public static void main(String... args) throws ExecutionException, InterruptedException {
        HazelcastInstance hazelcast = Hazelcast.newHazelcastInstance();

        try {
            ScheduledExecutorConfig defaultConfig =
                    hazelcast.getConfig().getScheduledExecutorConfig("default");
            System.out.printf("default pool-size = %d%n", defaultConfig.getPoolSize());
            System.out.printf("default durability = %d%n", defaultConfig.getDurability());
            System.out.printf("default capacity = %d%n", defaultConfig.getCapacity());

            ScheduledExecutorConfig customConfig =
                    hazelcast.getConfig().getScheduledExecutorConfig("configuredScheduledExecSvc");
            System.out.printf("custom pool-size = %d%n", customConfig.getPoolSize());
            System.out.printf("custom durability = %d%n", customConfig.getDurability());
            System.out.printf("custom capacity = %d%n", customConfig.getCapacity());

            IScheduledExecutorService es = hazelcast.getScheduledExecutorService("configuredScheduledExecSvc");
            IScheduledFuture<?> future = es.schedule(new HelloRunnableTask(), 0, TimeUnit.SECONDS);
            future.get();
        } finally {
            hazelcast.shutdown();
            Hazelcast.shutdownAll();
        }
    }
}
