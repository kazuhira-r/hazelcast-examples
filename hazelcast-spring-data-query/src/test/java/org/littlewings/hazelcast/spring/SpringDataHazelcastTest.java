package org.littlewings.hazelcast.spring;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.littlewings.hazelcast.spring.entity.Book;
import org.littlewings.hazelcast.spring.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = HazelcastConfig.class)
public class SpringDataHazelcastTest {
    List<Book> books =
            Arrays.asList(
                    Book.create("978-1785285332", "Getting Started With Hazelcast", 3812),
                    Book.create("978-4798142470", "Spring徹底入門 Spring FrameworkによるJavaアプリケーション開発", 4320),
                    Book.create("978-4774182179", "［改訂新版］Spring入門 ――Javaフレームワーク・より良い設計とアーキテクチャ", 4104),
                    Book.create("978-4777519699", "はじめてのSpring Boot―スプリング・フレームワークで簡単Javaアプリ開発", 2700)
            );

    @Autowired
    BookRepository bookRepository;

    @Test
    public void simpleQuery() {
        withOtherHazelcastInstances(2, () -> {
            bookRepository.save(books);

            List<Book> resultBooks = bookRepository.findByTitle(books.get(0).getTitle());

            assertThat(resultBooks).hasSize(1);
            assertThat(resultBooks.get(0).getIsbn()).isEqualTo("978-1785285332");
            assertThat(resultBooks.get(0).getTitle()).isEqualTo("Getting Started With Hazelcast");
        });
    }

    @Test
    public void greaterThanQuery() {
        withOtherHazelcastInstances(2, () -> {
            bookRepository.save(books);

            List<Book> resultBooks = bookRepository.findByPriceGreaterThan(4000);

            assertThat(resultBooks).hasSize(2);
            assertThat(resultBooks.get(0).getIsbn()).isEqualTo("978-4798142470");
            assertThat(resultBooks.get(0).getTitle()).isEqualTo("Spring徹底入門 Spring FrameworkによるJavaアプリケーション開発");
            assertThat(resultBooks.get(0).getPrice()).isEqualTo(4320);
            assertThat(resultBooks.get(1).getIsbn()).isEqualTo("978-4774182179");
            assertThat(resultBooks.get(1).getTitle()).isEqualTo("［改訂新版］Spring入門 ――Javaフレームワーク・より良い設計とアーキテクチャ");
            assertThat(resultBooks.get(1).getPrice()).isEqualTo(4104);
        });
    }

    @Test
    public void andQuery() {
        withOtherHazelcastInstances(2, () -> {
            bookRepository.save(books);

            List<Book> resultBooks1 =
                    bookRepository.findByIsbnAndGreaterThanPrice("978-1785285332", 3000);

            assertThat(resultBooks1).hasSize(1);
            assertThat(resultBooks1.get(0).getIsbn()).isEqualTo("978-1785285332");
            assertThat(resultBooks1.get(0).getTitle()).isEqualTo("Getting Started With Hazelcast");

            List<Book> resultBooks2 =
                    bookRepository.findByIsbnAndGreaterThanPrice("978-1785285332", 4000);

            assertThat(resultBooks2).isEmpty();
        });
    }

    void withOtherHazelcastInstances(int numInstances, Runnable runnable) {
        List<HazelcastInstance> hazelcasts =
                IntStream
                        .rangeClosed(1, numInstances)
                        .mapToObj(i -> Hazelcast.newHazelcastInstance())
                        .collect(Collectors.toList());

        try {
            runnable.run();
        } finally {
            hazelcasts.forEach(HazelcastInstance::shutdown);
        }
    }
}
