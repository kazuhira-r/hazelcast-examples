package org.littlewings.hazelcast;

import java.io.Serializable;

public class Language implements Serializable {
    private String name;

    public Language(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
