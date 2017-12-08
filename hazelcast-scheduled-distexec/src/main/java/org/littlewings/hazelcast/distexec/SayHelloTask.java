package org.littlewings.hazelcast.distexec;

import java.io.Serializable;
import java.time.LocalDateTime;

public class SayHelloTask implements Runnable, Serializable {
    @Override
    public void run() {
        System.out.printf("[%s] Hello!!%n", LocalDateTime.now());
    }
}
