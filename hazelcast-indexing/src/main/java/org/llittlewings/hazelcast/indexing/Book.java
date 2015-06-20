package org.llittlewings.hazelcast.indexing;

import java.io.Serializable;
import java.util.Objects;

public class Book implements Serializable {
    private static final long serialVersionUID = 1L;

    private String isbn;

    private String title;

    private int price;

    public Book(String isbn, String title, int price) {
        this.isbn = isbn;
        this.title = title;
        this.price = price;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public int getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Book) {
            Book other = (Book) o;

            return isbn.equals(other.isbn) && title.equals(other.title) && price == other.price;
        }

        return false;
     }

    @Override
    public int hashCode() {
        return Objects.hash(isbn, title, price);
    }
}
