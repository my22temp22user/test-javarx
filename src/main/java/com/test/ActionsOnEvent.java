package com.test;

import java.util.function.Consumer;

public class ActionsOnEvent {

    private final Consumer<String> actionOnEvent;
    private final Consumer<Throwable> actionOnError;

    public ActionsOnEvent(Consumer<String> actionOnEvent, Consumer<Throwable> actionOnError) {
        this.actionOnEvent = actionOnEvent;
        this.actionOnError = actionOnError;
    }

    public Consumer<String> getActionOnEvent() {
        return actionOnEvent;
    }

    public Consumer<Throwable> getActionOnError() {
        return actionOnError;
    }
}
