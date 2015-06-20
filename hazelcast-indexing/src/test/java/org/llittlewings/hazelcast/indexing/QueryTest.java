package org.llittlewings.hazelcast.indexing;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.hazelcast.core.IMap;
import com.hazelcast.core.PartitionService;
import com.hazelcast.query.SqlPredicate;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class QueryTest extends HazelcastTestSupport {
    @Test
    public void testQuery() {
        List<Book> books =
                Arrays.asList(
                        new Book("978-4774169316", "Javaエンジニア養成読本", 2138),
                        new Book("978-4798124605", "Beginning Java EE 6 GlassFish 3で始めるエンタープライズJava", 4536),
                        new Book("978-4873117188", "Javaパフォーマンス", 4212)
                );

        withHazelcast(3, hazelcast -> {
            IMap<String, Book> map = hazelcast.getMap("default");

            books.stream().forEach(b -> map.put(b.getIsbn(), b));

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

            SqlPredicate titleQuery = new SqlPredicate("title = 'Javaエンジニア養成読本'");
            Collection<Book> booksByTitleQuery = map.values(titleQuery);

            assertThat(booksByTitleQuery)
                    .hasSize(1)
                    .containsOnly(new Book("978-4774169316", "Javaエンジニア養成読本", 2138));

            SqlPredicate titleWithLikeQuery = new SqlPredicate("title LIKE '%Java%' AND title LIkE '%養成読本'");
            Collection<Book> booksByTitleWithLikeQuery = map.values(titleWithLikeQuery);

            assertThat(booksByTitleWithLikeQuery)
                    .hasSize(1)
                    .containsOnly(new Book("978-4774169316", "Javaエンジニア養成読本", 2138));

            SqlPredicate priceQuery = new SqlPredicate("price > 4000");
            Collection<Book> booksByPriceQuery = map.values(priceQuery);

            assertThat(booksByPriceQuery)
                    .hasSize(2)
                    .containsSequence(
                            new Book("978-4873117188", "Javaパフォーマンス", 4212),
                            new Book("978-4798124605", "Beginning Java EE 6 GlassFish 3で始めるエンタープライズJava", 4536)
                    );
        });
    }
}
