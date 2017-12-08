package org.littlewings.hazelcast.distexec;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;

public class HelloRunnableTask implements Runnable, HazelcastInstanceAware, Serializable {
    transient HazelcastInstance hazelcast;

    @Override
    public void setHazelcastInstance(HazelcastInstance hazelcast) {
        this.hazelcast = hazelcast;
    }

    @Override
    public void run() {
        System.out.printf("[%s] Hello from %s%n", LocalDateTime.now(), hazelcast.getCluster().getLocalMember().getUuid());
    }
}
