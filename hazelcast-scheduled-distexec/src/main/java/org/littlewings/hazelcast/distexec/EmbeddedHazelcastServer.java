package org.littlewings.hazelcast.distexec;

import java.time.LocalDateTime;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

public class EmbeddedHazelcastServer {
    public static void main(String... args) {
        HazelcastInstance hazelcast = Hazelcast.newHazelcastInstance();

        try {
            System.out.printf("[%s] startup, Embedded Hazelcast Server.%n", LocalDateTime.now());
            System.console().readLine("> Enter stop.");
        } finally {
            hazelcast.shutdown();
            Hazelcast.shutdownAll();
        }
    }
}
