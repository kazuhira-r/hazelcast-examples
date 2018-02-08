package org.littlewings.hazelcast.entryprocessor;

import java.io.Serializable;

import com.hazelcast.core.PartitionAware;

public class BookKey implements PartitionAware<String>, Serializable {
    private static final long serialVersionUID = 1L;

    String isbn;

    Category category;

    public static BookKey create(String isbn, Category category) {
        return new BookKey(isbn, category);
    }

    public BookKey(String isbn, Category category) {
        this.isbn = isbn;
        this.category = category;
    }

    public String getIsbn() {
        return isbn;
    }

    public Category getCategory() {
        return category;
    }

    @Override
    public String getPartitionKey() {
        return category.getName();
    }
}
