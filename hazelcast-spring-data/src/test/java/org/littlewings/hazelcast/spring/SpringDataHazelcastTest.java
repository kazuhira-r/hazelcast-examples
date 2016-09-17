package org.littlewings.hazelcast.spring;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.query.EntryObject;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.PredicateBuilder;
import com.hazelcast.query.Predicates;
import com.hazelcast.query.SqlPredicate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.littlewings.hazelcast.spring.entity.Book;
import org.littlewings.hazelcast.spring.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.keyvalue.core.KeyValueOperations;
import org.springframework.data.keyvalue.core.query.KeyValueQuery;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = HazelcastConfig.class)
public class SpringDataHazelcastTest {
    @Autowired
    BookRepository bookRepository;

    @Before
    public void setUp() {
        bookRepository.delete(bookRepository.findAll());
    }

    @Test
    public void saveAndFind() {
        bookRepository.save(Book.create("978-1785285332", "Getting Started With Hazelcast", 3848));

        assertThat(bookRepository.findOne("978-1785285332").getTitle())
                .isEqualTo("Getting Started With Hazelcast");

        assertThat(bookRepository.findAll())
                .hasSize(1);
        assertThat(bookRepository.count())
                .isEqualTo(1);
    }

    @Autowired
    HazelcastInstance hazelcast;

    @Test
    public void saveAndFindAndUnderlying() {
        bookRepository.save(Book.create("978-1785285332", "Getting Started With Hazelcast", 3848));

        assertThat(bookRepository.findOne("978-1785285332").getTitle())
                .isEqualTo("Getting Started With Hazelcast");

        assertThat(bookRepository.findAll())
                .hasSize(1);
        assertThat(bookRepository.count())
                .isEqualTo(1);

        // assertThat(hazelcast.getMap("org.littlewings.hazelcast.spring.entity.Book"))
        //         .hasSize(1);
        assertThat(hazelcast.getMap("books"))
                .hasSize(1);

        assertThat(hazelcast.<String, Book>getMap("books").get("978-1785285332").getTitle())
                .isEqualTo("Getting Started With Hazelcast");
    }

    @Test
    public void query() {
        bookRepository.save(Arrays.asList(Book.create("978-1785285332", "Getting Started With Hazelcast", 3848),
                Book.create("978-1782169970", "Infinispan Data Grid Platform Definitive Guide", 4947),
                Book.create("978-1783988181", "Mastering Redis", 6172)));

        assertThat(bookRepository.findByTitle("Getting Started With Hazelcast").getPrice())
                .isEqualTo(3848);

        List<Book> books = bookRepository.findByPriceGreaterThan(4000);

        assertThat(books.stream().map(Book::getTitle).collect(Collectors.toList()))
                .hasSize(2)
                .containsExactly("Infinispan Data Grid Platform Definitive Guide", "Mastering Redis");
    }

    @Autowired
    KeyValueOperations keyValueOperations;

    @Test
    public void usingPredicates() {
        bookRepository.save(Arrays.asList(Book.create("978-1785285332", "Getting Started With Hazelcast", 3848),
                Book.create("978-1782169970", "Infinispan Data Grid Platform Definitive Guide", 4947),
                Book.create("978-1783988181", "Mastering Redis", 6172)));

        Predicate<String, Book> predicate =
                Predicates.and(Predicates.equal("isbn", "978-1785285332"), Predicates.greaterEqual("price", 3000));

        KeyValueQuery<Predicate<String, Book>> query = new KeyValueQuery<>(predicate);

        Iterable<Book> books = keyValueOperations.find(query, Book.class);

        assertThat(StreamSupport.stream(books.spliterator(), false).map(Book::getTitle).collect(Collectors.toList()))
                .hasSize(1)
                .containsExactly("Getting Started With Hazelcast");
    }

    @Test
    public void usingPredicateBuilder() {
        bookRepository.save(Arrays.asList(Book.create("978-1785285332", "Getting Started With Hazelcast", 3848),
                Book.create("978-1782169970", "Infinispan Data Grid Platform Definitive Guide", 4947),
                Book.create("978-1783988181", "Mastering Redis", 6172)));

        EntryObject e = new PredicateBuilder().getEntryObject();
        Predicate<String, Book> predicate =
                e.get("isbn").equal("978-1785285332").and(e.get("price").greaterEqual(3000));

        KeyValueQuery<Predicate<String, Book>> query = new KeyValueQuery<>(predicate);

        Iterable<Book> books = keyValueOperations.find(query, Book.class);

        assertThat(StreamSupport.stream(books.spliterator(), false).map(Book::getTitle).collect(Collectors.toList()))
                .hasSize(1)
                .containsExactly("Getting Started With Hazelcast");
    }

    @Test
    public void usingSqlPredicate() {
        bookRepository.save(Arrays.asList(Book.create("978-1785285332", "Getting Started With Hazelcast", 3848),
                Book.create("978-1782169970", "Infinispan Data Grid Platform Definitive Guide", 4947),
                Book.create("978-1783988181", "Mastering Redis", 6172)));

        Predicate<String, Book> predicate = new SqlPredicate("price > 4000");
        KeyValueQuery<Predicate<String, Book>> query = new KeyValueQuery<>(predicate);
        query.setSort(new Sort(Sort.Direction.DESC, "price"));

        Iterable<Book> books = keyValueOperations.find(query, Book.class);

        assertThat(StreamSupport.stream(books.spliterator(), false).map(Book::getTitle).collect(Collectors.toList()))
                .hasSize(2)
                .containsExactly("Mastering Redis", "Infinispan Data Grid Platform Definitive Guide");
    }
}
