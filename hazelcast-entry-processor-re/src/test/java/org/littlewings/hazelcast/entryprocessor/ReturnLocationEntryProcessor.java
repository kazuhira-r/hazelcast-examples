package org.littlewings.hazelcast.entryprocessor;

import java.util.Map;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.core.PartitionService;
import com.hazelcast.map.AbstractEntryProcessor;
import com.hazelcast.map.LockAware;

public class ReturnLocationEntryProcessor extends AbstractEntryProcessor<String, String> implements HazelcastInstanceAware {
    transient HazelcastInstance hazelcast;

    @Override
    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        this.hazelcast = hazelcastInstance;
    }

    @Override
    public Object process(Map.Entry<String, String> entry) {
        PartitionService ps = hazelcast.getPartitionService();

        System.out.printf(
                "key = %s, run entry processor member = %s, owner? = %b, locked? = %b%n",
                entry.getKey(),
                hazelcast.getCluster().getLocalMember().getUuid(),
                ps.getPartition(entry.getKey()).getOwner().getUuid().equals(hazelcast.getCluster().getLocalMember().getUuid()),
                ((LockAware) entry).isLocked()
        );

        return hazelcast.getCluster().getLocalMember().getUuid();
    }
}
