package org.llittlewings.hazelcast.indexing;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.hazelcast.core.IMap;
import com.hazelcast.core.PartitionService;
import org.junit.Test;

public class IndexingTest extends HazelcastTestSupport {
    @Test
    public void indexingTest() {
        List<Book> books =
                Arrays.asList(
                        new Book("978-4774169316", "Javaエンジニア養成読本", 2138),
                        new Book("978-4798124605", "Beginning Java EE 6 GlassFish 3で始めるエンタープライズJava", 4536),
                        new Book("978-4873117188", "Javaパフォーマンス", 4212)
                );

        withHazelcast(3, hazelcast -> {
            IMap<String, Book> map = hazelcast.getMap("default");

            books.stream().forEach(b -> map.put(b.getIsbn(), b));

            try {
                System.out.println("Sleeping...");
                TimeUnit.SECONDS.sleep(10L);
            } catch (InterruptedException e) { }

            PartitionService ps = hazelcast.getPartitionService();
            System.out.printf(
                    "%s:%s => %s%n",
                    "978-4774169316",
                    "Javaエンジニア養成読本",
                    ps.getPartition("978-4774169316").getOwner()
            );
            System.out.printf(
                    "%s:%s => %s%n",
                    "978-4798124605",
                    "Beginning Java EE 6 GlassFish 3で始めるエンタープライズJava",
                    ps.getPartition("978-4798124605").getOwner()
            );
            System.out.printf(
                    "%s:%s => %s%n",
                    "978-4873117188",
                    "Javaパフォーマンス",
                    ps.getPartition("978-4873117188").getOwner()
            );
        });
    }
}
