package com.test;

import java.util.Set;

public class Metrics {

    private static final Metrics INSTANCE = new Metrics();

    public static Metrics getInstance() {
        return INSTANCE;
    }

    private Metrics() {
    }

    private final MapCounter eventTypeCounter = new MapCounter();
    private final MapCounter wordsInDataCounter = new MapCounter();

    public void receivedEventType(String eventType) {
        eventTypeCounter.increment(eventType);
    }

    public void receivedWordInData(String wordInData) {
        wordsInDataCounter.increment(wordInData);
    }

    public Long numOfEventType(String eventType) {
        return eventTypeCounter.count(eventType);
    }

    public Long numOfWordsInData(String wordInData) {
        return wordsInDataCounter.count(wordInData);
    }

    public MapCounter getEventTypeCounter() {
        return eventTypeCounter;
    }

    public MapCounter getWordsInDataCounter() {
        return wordsInDataCounter;
    }
}
