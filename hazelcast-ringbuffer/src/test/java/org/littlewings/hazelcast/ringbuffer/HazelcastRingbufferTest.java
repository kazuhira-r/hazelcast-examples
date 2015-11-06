package org.littlewings.hazelcast.ringbuffer;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.hazelcast.config.ClasspathXmlConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.ringbuffer.OverflowPolicy;
import com.hazelcast.ringbuffer.ReadResultSet;
import com.hazelcast.ringbuffer.Ringbuffer;
import com.hazelcast.ringbuffer.StaleSequenceException;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.fail;

public class HazelcastRingbufferTest {
    @Test
    public void testSimpleRingbuffer() {
        withHazelcast(1, hazelcast -> {
            Ringbuffer<String> ringbuffer = hazelcast.getRingbuffer("default");

            IntStream.rangeClosed(1, 100).forEach(i -> ringbuffer.add("element" + i));

            assertThat(ringbuffer.size())
                    .isEqualTo(100L);

            long headSequence = ringbuffer.headSequence();
            assertThat(headSequence)
                    .isEqualTo(0L);

            try {
                long currentSequence = headSequence;
                assertThat(ringbuffer.readOne(currentSequence))
                        .isEqualTo("element1");

                currentSequence++;

                String lastElement = null;

                while (ringbuffer.size() > 0) {
                    lastElement = ringbuffer.readOne(currentSequence);

                    assertThat(lastElement)
                            .isNotNull();

                    if (currentSequence == 99) {
                        break;
                    }

                    currentSequence++;
                }

                assertThat(lastElement)
                        .isEqualTo("element100");

                assertThat(ringbuffer.headSequence())
                        .isEqualTo(0L);
                assertThat(ringbuffer.tailSequence())
                        .isEqualTo(99L);
            } catch (InterruptedException e) {
                fail(e.getMessage());
            }
        });
    }

    @Test
    public void testRingbufferReadMany() {
        withHazelcast(1, hazelcast -> {
            Ringbuffer<String> ringbuffer = hazelcast.getRingbuffer("default");

            IntStream.rangeClosed(1, 100).forEach(i -> ringbuffer.add("element" + i));

            assertThat(ringbuffer.size())
                    .isEqualTo(100L);

            long headSequence = ringbuffer.headSequence();
            assertThat(headSequence)
                    .isEqualTo(0L);

            try {
                long currentSequence = headSequence;
                assertThat(ringbuffer.readOne(currentSequence))
                        .isEqualTo("element1");

                currentSequence++;

                ICompletableFuture<ReadResultSet<String>> completableFuture =
                        ringbuffer
                                .readManyAsync(currentSequence, 1, 10, null);

                ReadResultSet<String> readResultSet = completableFuture.get();
                int readCount = readResultSet.readCount();

                List<String> resultElements =
                        IntStream
                                .range(0, readCount)
                                .mapToObj(i -> readResultSet.get(i))
                                .collect(Collectors.toList());

                List<String> verifyResultElements =
                        IntStream
                                .range(2, readCount + 2)
                                .mapToObj(i -> "element" + i)
                                .collect(Collectors.toList());

                assertThat(resultElements)
                        .isEqualTo(verifyResultElements);

                currentSequence += readCount;

                assertThat(currentSequence)
                        .isEqualTo(headSequence + readCount + 1);

                assertThat(ringbuffer.tailSequence())
                        .isEqualTo(99L);
            } catch (InterruptedException | ExecutionException e) {
                fail(e.getMessage());
            }
        });
    }

    @Test
    public void testRingbufferOverflow() {
        withHazelcast(1, hazelcast -> {
            Ringbuffer<String> ringbuffer = hazelcast.getRingbuffer("small-ringbuffer");

            IntStream.rangeClosed(1, 100).forEach(i -> ringbuffer.add("element" + i));

            assertThat(ringbuffer.size())
                    .isEqualTo(10L);

            long headSequence = ringbuffer.headSequence();
            assertThat(headSequence)
                    .isEqualTo(90L);

            try {
                long currentSequence = headSequence;
                assertThat(ringbuffer.readOne(currentSequence))
                        .isEqualTo("element91");

                currentSequence++;

                String lastElement = null;

                while (ringbuffer.size() > 0) {
                    lastElement = ringbuffer.readOne(currentSequence);

                    assertThat(lastElement)
                            .isNotNull();

                    if (currentSequence == 99) {
                        break;
                    }

                    currentSequence++;
                }

                assertThat(lastElement)
                        .isEqualTo("element100");

                assertThat(ringbuffer.headSequence())
                        .isEqualTo(90L);
                assertThat(ringbuffer.tailSequence())
                        .isEqualTo(99L);
            } catch (InterruptedException e) {
                fail(e.getMessage());
            }
        });
    }

    @Test
    public void testRingbufferSize() {
        withHazelcast(1, hazelcast -> {
            Ringbuffer<String> ringbuffer = hazelcast.getRingbuffer("default");

            IntStream.rangeClosed(1, 100).forEach(i -> ringbuffer.add("element" + i));

            assertThat(ringbuffer.size())
                    .isEqualTo(100L);

            long headSequence = ringbuffer.headSequence();
            assertThat(headSequence)
                    .isEqualTo(0L);

            long tailSequence = ringbuffer.tailSequence();
            assertThat(tailSequence)
                    .isEqualTo(99L);

            assertThat(ringbuffer.capacity())
                    .isEqualTo(10000L);

            assertThat(ringbuffer.remainingCapacity())
                    .isEqualTo(10000L);

            try {
                long currentSequence = headSequence;
                assertThat(ringbuffer.readOne(currentSequence))
                        .isEqualTo("element1");

                currentSequence++;

                ICompletableFuture<ReadResultSet<String>> completableFuture =
                        ringbuffer
                                .readManyAsync(currentSequence, 5, 10, null);

                ReadResultSet<String> readResultSet = completableFuture.get();
                int readCount = readResultSet.readCount();

                currentSequence += readCount;

                String lastElement = null;

                while (ringbuffer.size() > 0) {
                    lastElement = ringbuffer.readOne(currentSequence);

                    assertThat(lastElement)
                            .isNotNull();

                    if (currentSequence == 99) {
                        break;
                    }

                    currentSequence++;
                }

                assertThat(lastElement)
                        .isEqualTo("element100");

                assertThat(ringbuffer.headSequence())
                        .isEqualTo(0L);

                assertThat(ringbuffer.tailSequence())
                        .isEqualTo(99L);

                assertThat(ringbuffer.capacity())
                        .isEqualTo(10000L);

                assertThat(ringbuffer.remainingCapacity())
                        .isEqualTo(10000L);
            } catch (InterruptedException | ExecutionException e) {
                fail(e.getMessage());
            }
        });
    }

    @Test
    public void testRingbufferSizeWithExpiry() {
        withHazelcast(1, hazelcast -> {
            Ringbuffer<String> ringbuffer = hazelcast.getRingbuffer("with-expired");

            IntStream.rangeClosed(1, 100).forEach(i -> ringbuffer.add("element" + i));

            assertThat(ringbuffer.size())
                    .isEqualTo(100L);

            long headSequence = ringbuffer.headSequence();
            assertThat(headSequence)
                    .isEqualTo(0L);

            long tailSequence = ringbuffer.tailSequence();
            assertThat(tailSequence)
                    .isEqualTo(99L);

            assertThat(ringbuffer.capacity())
                    .isEqualTo(10000L);

            assertThat(ringbuffer.remainingCapacity())
                    .isEqualTo(9900L);

            try {
                long currentSequence = headSequence;
                assertThat(ringbuffer.readOne(currentSequence))
                        .isEqualTo("element1");

                currentSequence++;

                ICompletableFuture<ReadResultSet<String>> completableFuture =
                        ringbuffer
                                .readManyAsync(currentSequence, 5, 10, null);

                ReadResultSet<String> readResultSet = completableFuture.get();
                int readCount = readResultSet.readCount();

                currentSequence += readCount;

                String lastElement = null;

                while (ringbuffer.size() > 0) {
                    lastElement = ringbuffer.readOne(currentSequence);

                    assertThat(lastElement)
                            .isNotNull();

                    if (currentSequence == 99) {
                        break;
                    }

                    currentSequence++;
                }

                assertThat(lastElement)
                        .isEqualTo("element100");

                assertThat(ringbuffer.headSequence())
                        .isEqualTo(0L);

                assertThat(ringbuffer.tailSequence())
                        .isEqualTo(99L);

                assertThat(ringbuffer.capacity())
                        .isEqualTo(10000L);

                assertThat(ringbuffer.remainingCapacity())
                        .isEqualTo(9900L);
            } catch (InterruptedException | ExecutionException e) {
                fail(e.getMessage());
            }
        });
    }

    @Test
    public void testRingbufferWithExpiry() {
        withHazelcast(1, hazelcast -> {
            Ringbuffer<String> ringbuffer = hazelcast.getRingbuffer("with-expired");

            IntStream.rangeClosed(1, 100).forEach(i -> ringbuffer.add("element" + i));

            assertThat(ringbuffer.size())
                    .isEqualTo(100L);

            long headSequence = ringbuffer.headSequence();
            assertThat(headSequence)
                    .isEqualTo(0L);

            long tailSequence = ringbuffer.tailSequence();
            assertThat(tailSequence)
                    .isEqualTo(99L);

            assertThat(ringbuffer.capacity())
                    .isEqualTo(10000L);

            assertThat(ringbuffer.remainingCapacity())
                    .isEqualTo(9900L);

            try {
                long currentSequence = headSequence;
                assertThat(ringbuffer.readOne(currentSequence))
                        .isEqualTo("element1");

                currentSequence++;

                ICompletableFuture<ReadResultSet<String>> completableFuture =
                        ringbuffer
                                .readManyAsync(currentSequence, 5, 10, null);

                ReadResultSet<String> readResultSet = completableFuture.get();
                int readCount = readResultSet.readCount();

                currentSequence += readCount;

                TimeUnit.SECONDS.sleep(5);

                long seq1 = currentSequence++;
                assertThatThrownBy(() -> ringbuffer.readOne(seq1))
                        .isInstanceOf(StaleSequenceException.class);

                long seq2 = currentSequence++;
                assertThatThrownBy(() -> ringbuffer.readOne(seq2))
                        .isInstanceOf(StaleSequenceException.class);

                assertThat(ringbuffer.headSequence())
                        .isEqualTo(100L);

                assertThat(ringbuffer.tailSequence())
                        .isEqualTo(99L);

                assertThat(ringbuffer.capacity())
                        .isEqualTo(10000L);

                assertThat(ringbuffer.remainingCapacity())
                        .isEqualTo(10000L);
            } catch (InterruptedException | ExecutionException e) {
                fail(e.getMessage());
            }
        });
    }

    @Test
    public void testRingbufferOverflowFail() {
        withHazelcast(1, hazelcast -> {
            Ringbuffer<String> ringbuffer = hazelcast.getRingbuffer("small-ringbuffer");

            List<ICompletableFuture<Long>> futures =
                    IntStream
                            .rangeClosed(1, 100)
                            .mapToObj(i -> ringbuffer.addAsync("element" + i, OverflowPolicy.FAIL))
                            .collect(Collectors.toList());

            List<Long> results =
                    futures
                            .stream()
                            .map(f -> {
                                try {
                                    return f.get();
                                } catch (InterruptedException | ExecutionException e) {
                                    throw new RuntimeException(e);
                                }
                            })
                            .collect(Collectors.toList());

            assertThat(results)
                    .hasSize(100);
            assertThat(results)
                    .doesNotContain(-1L);

            try {
                long headSequence = ringbuffer.headSequence();
                assertThat(ringbuffer.readOne(headSequence))
                        .isEqualTo("element91");
                assertThat(headSequence)
                        .isEqualTo(90L);

                long tailSequence = ringbuffer.tailSequence();
                assertThat(ringbuffer.readOne(tailSequence))
                        .isEqualTo("element100");
                assertThat(tailSequence)
                        .isEqualTo(99L);
            } catch (InterruptedException e) {
                fail(e.getMessage());
            }
        });
    }

    @Test
    public void testRingbufferOverflowOverwrite() {
        withHazelcast(1, hazelcast -> {
            Ringbuffer<String> ringbuffer = hazelcast.getRingbuffer("small-ringbuffer");

            List<ICompletableFuture<Long>> futures =
                    IntStream
                            .rangeClosed(1, 100)
                            .mapToObj(i -> ringbuffer.addAsync("element" + i, OverflowPolicy.OVERWRITE))
                            .collect(Collectors.toList());

            List<Long> results =
                    futures
                            .stream()
                            .map(f -> {
                                try {
                                    return f.get();
                                } catch (InterruptedException | ExecutionException e) {
                                    throw new RuntimeException(e);
                                }
                            })
                            .collect(Collectors.toList());

            assertThat(results)
                    .hasSize(100);
            assertThat(results)
                    .doesNotContain(-1L);

            try {
                long headSequence = ringbuffer.headSequence();
                assertThat(ringbuffer.readOne(headSequence))
                        .isEqualTo("element91");
                assertThat(headSequence)
                        .isEqualTo(90L);

                long tailSequence = ringbuffer.tailSequence();
                assertThat(ringbuffer.readOne(tailSequence))
                        .isEqualTo("element100");
                assertThat(tailSequence)
                        .isEqualTo(99L);
            } catch (InterruptedException e) {
                fail(e.getMessage());
            }
        });
    }

    @Test
    public void testRingbufferOverflowFailWithExpiry() {
        withHazelcast(1, hazelcast -> {
            Ringbuffer<String> ringbuffer = hazelcast.getRingbuffer("small-ringbuffer-expired");

            List<ICompletableFuture<Long>> futures =
                    IntStream
                            .rangeClosed(1, 100)
                            .mapToObj(i -> ringbuffer.addAsync("element" + i, OverflowPolicy.FAIL))
                            .collect(Collectors.toList());

            List<Long> results =
                    futures
                            .stream()
                            .map(f -> {
                                try {
                                    return f.get();
                                } catch (InterruptedException | ExecutionException e) {
                                    throw new RuntimeException(e);
                                }
                            })
                            .collect(Collectors.toList());

            assertThat(results)
                    .hasSize(100);
            assertThat(results)
                    .contains(-1L);

            try {
                long headSequence = ringbuffer.headSequence();
                assertThat(ringbuffer.readOne(headSequence))
                        .isEqualTo("element1");
                assertThat(headSequence)
                        .isEqualTo(0L);

                long tailSequence = ringbuffer.tailSequence();
                assertThat(ringbuffer.readOne(tailSequence))
                        .isEqualTo("element10");
                assertThat(tailSequence)
                        .isEqualTo(9L);
            } catch (InterruptedException e) {
                fail(e.getMessage());
            }
        });
    }

    @Test
    public void testRingbufferOverflowOverwriteWithExpiry() {
        withHazelcast(1, hazelcast -> {
            Ringbuffer<String> ringbuffer = hazelcast.getRingbuffer("small-ringbuffer-expired");

            List<ICompletableFuture<Long>> futures =
                    IntStream
                            .rangeClosed(1, 100)
                            .mapToObj(i -> ringbuffer.addAsync("element" + i, OverflowPolicy.OVERWRITE))
                            .collect(Collectors.toList());

            List<Long> results =
                    futures
                            .stream()
                            .map(f -> {
                                try {
                                    return f.get();
                                } catch (InterruptedException | ExecutionException e) {
                                    throw new RuntimeException(e);
                                }
                            })
                            .collect(Collectors.toList());

            assertThat(results)
                    .hasSize(100);
            assertThat(results)
                    .doesNotContain(-1L);

            try {
                long headSequence = ringbuffer.headSequence();
                assertThat(ringbuffer.readOne(headSequence))
                        .isEqualTo("element91");
                assertThat(headSequence)
                        .isEqualTo(90L);

                long tailSequence = ringbuffer.tailSequence();
                assertThat(ringbuffer.readOne(tailSequence))
                        .isEqualTo("element100");
                assertThat(tailSequence)
                        .isEqualTo(99L);
            } catch (InterruptedException e) {
                fail(e.getMessage());
            }
        });
    }

    protected void withHazelcast(int numInstances, Consumer<HazelcastInstance> consumer) {
        List<HazelcastInstance> hazelcastInstances =
                IntStream
                        .rangeClosed(1, numInstances)
                        .mapToObj(i -> Hazelcast.newHazelcastInstance(new ClasspathXmlConfig("hazelcast.xml")))
                        .collect(Collectors.toList());

        try {
            consumer.accept(hazelcastInstances.get(0));
        } finally {
            hazelcastInstances.forEach(h -> h.getLifecycleService().shutdown());
            Hazelcast.shutdownAll();
        }
    }
}
