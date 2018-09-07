package com.test;

import java.util.function.Consumer;

public interface IConsumer {

    Consumer<String> onEvent();
}
