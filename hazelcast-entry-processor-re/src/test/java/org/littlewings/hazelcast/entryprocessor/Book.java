package org.littlewings.hazelcast.entryprocessor;

import java.io.Serializable;

public class Book implements Serializable {
    private static final long serialVersionUID = 1L;

    String isbn;
    String title;
    int price;
    Category category;

    public static Book create(String isbn, String title, int price, Category category) {
        return new Book(isbn, title, price, category);
    }

    public Book(String isbn, String title, int price, Category category) {
        this.isbn = isbn;
        this.title = title;
        this.price = price;
        this.category = category;
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

    public Category getCategory() {
        return category;
    }
}
