package org.littlewings.hazelcast.entryprocessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.map.AbstractEntryProcessor;

public class CalcPriceEntryProcessor extends AbstractEntryProcessor<String, Integer> implements HazelcastInstanceAware {
    transient HazelcastInstance hazelcast;

    List<String> isbnList;

    public CalcPriceEntryProcessor(List<String> isbnList) {
        super(false);
        this.isbnList = new ArrayList<>(isbnList);
    }

    @Override
    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        this.hazelcast = hazelcastInstance;
    }

    @Override
    public Object process(Map.Entry<String, Integer> entry) {
        Category category = Category.create(entry.getKey());

        Map<String, Book> bookMap = hazelcast.getMap("books");
        return isbnList.stream().mapToInt(isbn -> bookMap.get(BookKey.create(isbn, category)).getPrice()).sum();
    }
}
