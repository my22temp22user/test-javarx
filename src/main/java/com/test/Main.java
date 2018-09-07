package com.test;

import org.apache.log4j.Logger;

public class Main {

    private static final Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) throws Exception {

        logger.info("starting app");

        new AppServer().start();

        new JsonProducer().addSubscriber(new JsonConsumer()).start();
    }
}
