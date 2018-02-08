package org.littlewings.hazelcast.entryprocessor;

import java.io.Serializable;

public class Category implements Serializable {
    private static final long serialVersionUID = 1L;

    String name;

    public static Category create(String name) {
        return new Category(name);
    }

    public Category(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
