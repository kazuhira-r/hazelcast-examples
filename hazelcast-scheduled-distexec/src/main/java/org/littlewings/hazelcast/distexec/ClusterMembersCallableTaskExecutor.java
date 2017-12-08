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

public class ClusterMembersCallableTaskExecutor {
    public static void main(String... args) throws ExecutionException, InterruptedException {
        HazelcastInstance hazelcast = Hazelcast.newHazelcastInstance();

        try {
            Set<Member> members = new HashSet<>(hazelcast.getCluster().getMembers());
            members.remove(hazelcast.getCluster().getLocalMember());

            IScheduledExecutorService es = hazelcast.getScheduledExecutorService("default");
            Map<Member, IScheduledFuture<String>> futures =
                    es.scheduleOnMembers(new HelloCallableTask(), members, 5, TimeUnit.SECONDS);

            for (Map.Entry<Member, IScheduledFuture<String>> entry : futures.entrySet()) {
                System.out.printf("member[%s] from message = %s%n", entry.getKey().getUuid(), entry.getValue().get());
            }

            System.out.printf("self = %s%n", hazelcast.getCluster().getLocalMember().getUuid());
        } finally {
            hazelcast.shutdown();
            Hazelcast.shutdownAll();
        }
    }
}
