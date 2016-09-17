package org.littlewings.hazelcast.spring.repository;

import java.util.List;

import org.littlewings.hazelcast.spring.entity.Book;
import org.springframework.data.hazelcast.repository.HazelcastRepository;

public interface BookRepository extends HazelcastRepository<Book, String> {
    Book findByTitle(String title);

    List<Book> findByPriceGreaterThan(int price);
}
