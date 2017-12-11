package org.littlewings.hazelcast.spring.repository;

import java.util.List;

import org.littlewings.hazelcast.spring.entity.Book;
import org.springframework.data.hazelcast.repository.HazelcastRepository;
import org.springframework.data.hazelcast.repository.query.Query;

public interface BookRepository extends HazelcastRepository<Book, String> {
    // Book findByTitle(String title);

    // List<Book> findByPriceGreaterThan(int price);

    @Query("title = '%s'")
    List<Book> findByTitle(String title);

    @Query("price > %d")
    List<Book> findByPriceGreaterThan(int price);

    @Query("isbn = '%s' and price > %d")
    List<Book> findByIsbnAndGreaterThanPrice(String isbn, int price);
}
