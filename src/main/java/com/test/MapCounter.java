package com.test;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MapCounter {

    private final ConcurrentMap<String, Long> keyToCounter = new ConcurrentHashMap<>();

    public void increment(String str) {
        keyToCounter.compute(str, (key, oldValue) -> oldValue == null ? 1L : oldValue+ 1L);
    }

    public long count(String str) {
        Long counter = keyToCounter.get(str);

        return counter == null ? 0 : counter;
    }

    @Override
    public String toString() {
        return keyToCounter.toString();
    }
}
