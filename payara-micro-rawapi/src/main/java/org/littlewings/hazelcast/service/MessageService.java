package org.littlewings.hazelcast.service;

import java.util.concurrent.TimeUnit;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.hazelcast.core.IMap;

@ApplicationScoped
public class MessageService {
    @Inject
    private IMap<String, String> simpleMap;

    public String build(String key, String word) {
        if (simpleMap.containsKey(key)) {
            return simpleMap.get(key);
        }

        try {
            TimeUnit.SECONDS.sleep(3L);
        } catch (InterruptedException e) {
        }

        String message = String.format("Hello %s!!", word);

        simpleMap.put(key, message);
        return simpleMap.get(key);
    }
}
