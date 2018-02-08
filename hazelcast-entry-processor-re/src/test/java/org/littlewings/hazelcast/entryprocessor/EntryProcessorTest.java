package org.littlewings.hazelcast.entryprocessor;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.PartitionService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EntryProcessorTest {
    @Test
    public void confirmLocationRunEntryProcessor() {
        withHazelcastInstance(3, hazelcast -> {
            IMap<String, String> map = hazelcast.getMap("default");

            IntStream.rangeClosed(1, 10).forEach(i -> map.put("key" + i, "value" + i));

            PartitionService ps = hazelcast.getPartitionService();

            IntStream
                    .rangeClosed(1, 10)
                    .forEach(i -> {
                        String key = "key" + i;
                        System.out.printf("key = %s, location = %s%n", key, ps.getPartition(key).getOwner().getUuid());

                        assertThat(
                                map.executeOnKey(key, new ReturnLocationEntryProcessor())
                        ).isEqualTo(ps.getPartition(key).getOwner().getUuid());
                    });
        });
    }

    @Test
    public void dataAffinity() {
        Category springCategory = Category.create("spring");
        Category javaeeCategory = Category.create("javaee");

        Book[] springBooks = {
                Book.create("978-4798142470", "Spring徹底入門 Spring FrameworkによるJavaアプリケーション開発", 4320, springCategory),
                Book.create("978-4774182179", "[改訂新版]Spring入門 ――Javaフレームワーク・より良い設計とアーキテクチャ", 4104, springCategory),
                Book.create("978-4777519699", "はじめてのSpring Boot―スプリング・フレームワークで簡単Javaアプリ開発", 2700, springCategory)
        };

        Book[] javaeeBooks = {
                Book.create("978-4774183169", "パーフェクト Java EE", 3456, javaeeCategory),
                Book.create("978-4798140926", "Java EE 7徹底入門 標準Javaフレームワークによる高信頼性Webシステムの構築", 4104, javaeeCategory),
                Book.create("978-4798124605", "Beginning Java EE 6～GlassFish 3で始めるエンタープライズJava", 4536, javaeeCategory)
        };

        withHazelcastInstance(3, hazelcast -> {
            IMap<String, Category> categoryMap = hazelcast.getMap("categories");
            IMap<BookKey, Book> bookMap = hazelcast.getMap("books");

            categoryMap.put(springCategory.getName(), springCategory);
            categoryMap.put(javaeeCategory.getName(), javaeeCategory);

            Arrays.stream(springBooks).forEach(book -> bookMap.put(BookKey.create(book.getIsbn(), book.getCategory()), book));
            Arrays.stream(javaeeBooks).forEach(book -> bookMap.put(BookKey.create(book.getIsbn(), book.getCategory()), book));

            PartitionService ps = hazelcast.getPartitionService();

            assertThat(
                    Arrays
                            .stream(springBooks)
                            .map(book -> ps.getPartition(BookKey.create(book.getIsbn(), book.getCategory())).getOwner().getUuid())
                            .collect(Collectors.toSet())
            )
                    .hasSize(1)
                    .containsOnly(ps.getPartition(springCategory.getName()).getOwner().getUuid());
            assertThat(
                    Arrays
                            .stream(springBooks)
                            .map(book -> ps.getPartition(BookKey.create(book.getIsbn(), book.getCategory())).getPartitionId())
                            .collect(Collectors.toSet())
            )
                    .hasSize(1)
                    .containsOnly(ps.getPartition(springCategory.getName()).getPartitionId());

            assertThat(
                    Arrays
                            .stream(javaeeBooks)
                            .map(book -> ps.getPartition(BookKey.create(book.getIsbn(), book.getCategory())).getOwner().getUuid())
                            .collect(Collectors.toSet())
            )
                    .hasSize(1)
                    .containsOnly(ps.getPartition(javaeeCategory.getName()).getOwner().getUuid());
            assertThat(
                    Arrays
                            .stream(javaeeBooks)
                            .map(book -> ps.getPartition(BookKey.create(book.getIsbn(), book.getCategory())).getPartitionId())
                            .collect(Collectors.toSet())
            )
                    .hasSize(1)
                    .containsOnly(ps.getPartition(javaeeCategory.getName()).getPartitionId());
        });
    }

    @Test
    public void calcPrice() {
        Category springCategory = Category.create("spring");
        Category javaeeCategory = Category.create("javaee");

        Book[] springBooks = {
                Book.create("978-4798142470", "Spring徹底入門 Spring FrameworkによるJavaアプリケーション開発", 4320, springCategory),
                Book.create("978-4774182179", "[改訂新版]Spring入門 ――Javaフレームワーク・より良い設計とアーキテクチャ", 4104, springCategory),
                Book.create("978-4777519699", "はじめてのSpring Boot―スプリング・フレームワークで簡単Javaアプリ開発", 2700, springCategory)
        };

        Book[] javaeeBooks = {
                Book.create("978-4774183169", "パーフェクト Java EE", 3456, javaeeCategory),
                Book.create("978-4798140926", "Java EE 7徹底入門 標準Javaフレームワークによる高信頼性Webシステムの構築", 4104, javaeeCategory),
                Book.create("978-4798124605", "Beginning Java EE 6～GlassFish 3で始めるエンタープライズJava", 4536, javaeeCategory)
        };

        withHazelcastInstance(3, hazelcast -> {
            IMap<String, Category> categoryMap = hazelcast.getMap("categories");
            IMap<BookKey, Book> bookMap = hazelcast.getMap("books");

            categoryMap.put(springCategory.getName(), springCategory);
            categoryMap.put(javaeeCategory.getName(), javaeeCategory);

            Arrays.stream(springBooks).forEach(book -> bookMap.put(BookKey.create(book.getIsbn(), book.getCategory()), book));
            Arrays.stream(javaeeBooks).forEach(book -> bookMap.put(BookKey.create(book.getIsbn(), book.getCategory()), book));

            assertThat(
                    categoryMap.executeOnKey(
                            springCategory.getName(),
                            new CalcPriceEntryProcessor(Arrays.stream(springBooks).map(Book::getIsbn).collect(Collectors.toList()))
                    )
            ).isEqualTo(11124);

            assertThat(
                    categoryMap.executeOnKey(
                            javaeeCategory.getName(),
                            new CalcPriceEntryProcessor(Arrays.stream(javaeeBooks).map(Book::getIsbn).collect(Collectors.toList()))
                    )
            ).isEqualTo(12096);

        });
    }

    void withHazelcastInstance(int numInstances, Consumer<HazelcastInstance> fun) {
        List<HazelcastInstance> instances =
                IntStream
                        .rangeClosed(1, numInstances)
                        .mapToObj(i -> Hazelcast.newHazelcastInstance())
                        .collect(Collectors.toList());

        try {
            fun.accept(instances.get(0));
        } finally {
            instances.forEach(HazelcastInstance::shutdown);
            Hazelcast.shutdownAll();
        }
    }
}
