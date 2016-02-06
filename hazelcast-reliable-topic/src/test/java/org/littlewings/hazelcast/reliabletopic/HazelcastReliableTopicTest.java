package org.littlewings.hazelcast.reliabletopic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.hazelcast.config.ClasspathXmlConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import com.hazelcast.topic.TopicOverloadException;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class HazelcastReliableTopicTest {
    @Test
    public void testSimpleUsage() {
        withHazelcast(3, hazelcastInstance -> {
            ITopic<String> topic = hazelcastInstance.getReliableTopic("default");

            List<String> receivedMessage = new ArrayList<>();
            topic.addMessageListener(message -> receivedMessage.add(message.getMessageObject()));

            topic.publish("Hello World");
            topic.publish("Hello Hazelcast!");

            assertThat(receivedMessage)
                    .isEqualTo(Arrays.asList("Hello World", "Hello Hazelcast!"));

            assertThat(topic.getLocalTopicStats().getPublishOperationCount())
                    .isEqualTo(2L);
            assertThat(topic.getLocalTopicStats().getReceiveOperationCount())
                    .isEqualTo(2L);
        });
    }

    @Test
    public void testWithRingbufferTtl() {
        withHazelcast(3, hazelcastInstance -> {
            ITopic<String> topic = hazelcastInstance.getReliableTopic("with-ttl");

            long start = System.currentTimeMillis();

            IntStream
                    .rangeClosed(1, 10005)
                    .forEach(i -> topic.publish("message-" + i));

            long elapsed = System.currentTimeMillis() - start;

            assertThat(topic.getLocalTopicStats().getPublishOperationCount())
                    .isEqualTo(10005L);
            assertThat(topic.getLocalTopicStats().getReceiveOperationCount())
                    .isEqualTo(0L);

            assertThat(elapsed)
                    .isGreaterThanOrEqualTo(30 * 1000L);
        });
    }

    @Test
    public void testOverloadError() {
        withHazelcast(3, hazelcastInstance -> {
            ITopic<String> topic = hazelcastInstance.getReliableTopic("overload-error-policy");

            List<String> receivedMessage = new ArrayList<>();
            topic.addMessageListener(message -> {
                receivedMessage.add(message.getMessageObject());

                try {
                    TimeUnit.MICROSECONDS.sleep(500L);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });

            assertThatThrownBy(() -> {
                        IntStream
                                .rangeClosed(1, 15)
                                .forEach(i -> topic.publish("message-" + i));

                        try {
                            TimeUnit.SECONDS.sleep(10L);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
            )
                    .isInstanceOf(TopicOverloadException.class)
                    .hasMessageContaining("Failed to publish message:");
        });
    }

    @Test
    public void testOverloadDiscardNewest() {
        withHazelcast(3, hazelcastInstance -> {
            ITopic<String> topic = hazelcastInstance.getReliableTopic("overload-discard-newest");

            List<String> receivedMessage = new ArrayList<>();
            topic.addMessageListener(message -> {
                receivedMessage.add(message.getMessageObject());

                try {
                    TimeUnit.SECONDS.sleep(1L);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });

            IntStream
                    .rangeClosed(1, 15)
                    .forEach(i -> topic.publish("message-" + i));

            try {
                TimeUnit.SECONDS.sleep(20);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            assertThat(receivedMessage)
                    .containsExactly("message-1",
                            "message-2",
                            "message-3",
                            "message-4",
                            "message-5",
                            "message-6",
                            "message-7",
                            "message-8",
                            "message-9",
                            "message-10");
        });
    }

    @Test
    public void testOverloadDiscardOldest() {
        withHazelcast(3, hazelcastInstance -> {
            ITopic<String> topic = hazelcastInstance.getReliableTopic("overload-discard-oldest");

            List<String> receivedMessage = new ArrayList<>();
            topic.addMessageListener(message -> {
                receivedMessage.add(message.getMessageObject());

                try {
                    TimeUnit.SECONDS.sleep(1L);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });

            IntStream
                    .rangeClosed(1, 15)
                    .forEach(i -> topic.publish("message-" + i));

            try {
                TimeUnit.SECONDS.sleep(20L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            assertThat(receivedMessage)
                    .containsExactly("message-1");
        });
    }

    protected void withHazelcast(int numInstances, Consumer<HazelcastInstance> f) {
        List<HazelcastInstance> hazelcastInstances =
                IntStream
                        .rangeClosed(1, numInstances)
                        .mapToObj(i -> Hazelcast.newHazelcastInstance(new ClasspathXmlConfig("hazelcast.xml")))
                        .collect(Collectors.toList());

        try {
            f.accept(hazelcastInstances.get(0));
        } finally {
            hazelcastInstances.forEach(h -> h.getLifecycleService().shutdown());
            Hazelcast.shutdownAll();
        }
    }
}
