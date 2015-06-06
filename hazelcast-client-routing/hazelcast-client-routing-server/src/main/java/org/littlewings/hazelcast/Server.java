package org.littlewings.hazelcast;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.hazelcast.config.ClasspathXmlConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

public class Server {
    public static void main(String... args) {
        HazelcastInstance hazelcast =
                Hazelcast.newHazelcastInstance(new ClasspathXmlConfig("hazelcast.xml"));

        try {
            System
                    .console()
                    .readLine("[%s] Hazelcast Server Startup.",
                            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        } finally {
            hazelcast.getLifecycleService().shutdown();
            Hazelcast.shutdownAll();
        }
    }
}
