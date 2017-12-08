package org.littlewings.hazelcast.distexec;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.Member;
import com.hazelcast.scheduledexecutor.IScheduledExecutorService;
import com.hazelcast.scheduledexecutor.IScheduledFuture;

public class AllMemberRepeatTaskExecutor {
    public static void main(String... args) throws ExecutionException, InterruptedException {
        HazelcastInstance hazelcast = Hazelcast.newHazelcastInstance();

        try {
            IScheduledExecutorService es = hazelcast.getScheduledExecutorService("default");

            Map<Member,IScheduledFuture<?>> futures =
                    es.scheduleOnAllMembersAtFixedRate(new HelloRunnableTask(), 5, 5, TimeUnit.SECONDS);

            System.console().readLine("> Enter stop tasks.");

            for (Map.Entry<Member, IScheduledFuture<?>> entry : futures.entrySet()) {
                entry.getValue().cancel(false);
            }

            es.shutdown();

            System.out.println("Task Finish!!");
        } finally {
            hazelcast.shutdown();
            Hazelcast.shutdownAll();
        }
    }
}
