package org.littlewings.hazelcast.distexec;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.concurrent.Callable;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;

public class HelloCallableTask implements Callable<String>, HazelcastInstanceAware, Serializable {
    transient HazelcastInstance hazelcast;

    @Override
    public void setHazelcastInstance(HazelcastInstance hazelcast) {
        this.hazelcast = hazelcast;
    }

    @Override
    public String call() throws Exception {
        return String.format("[%s] Hello from %s!!", LocalDateTime.now(), hazelcast.getCluster().getLocalMember().getUuid());
    }
}
