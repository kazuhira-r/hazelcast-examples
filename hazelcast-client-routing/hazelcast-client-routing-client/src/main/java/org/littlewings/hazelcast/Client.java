package org.littlewings.hazelcast;

import java.io.IOException;
import java.util.Objects;
import java.util.stream.Stream;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.XmlClientConfigBuilder;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

public class Client {
    public static void main(String... args) throws IOException {
        HazelcastInstance client =
                HazelcastClient.newHazelcastClient(new XmlClientConfigBuilder("src/main/resources/hazelcast-client.xml").build());

        try {
            IMap<String, Language> map = client.getMap("language");

            Stream
                    .generate(() -> System.console().readLine("Command> "))
                    .filter(Objects::nonNull)
                    .forEach(line -> {
                        String[] tokens = line.split("\\s+");
                        String command = tokens[0];

                        switch (command) {
                            case "put":
                                String key = tokens[1];
                                Language language = new Language(tokens[2]);
                                map.put(key, language);
                                System.out.printf("putted %s/%s%n", key, language);
                                break;
                            case "get":
                                System.out.printf("get => %s%n", map.get(tokens[1]));
                                break;
                            default:
                                System.out.printf("Unknown command => %s%n", command);
                                break;
                        }
                    });
        } finally {
            client.getLifecycleService().shutdown();
            HazelcastClient.shutdownAll();
        }
    }
}
