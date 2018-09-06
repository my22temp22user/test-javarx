package com.test;

import static com.test.Constants.PORT;

public class Configuration {

    private static Configuration INSTANCE = new Configuration();

    public static Configuration getInstance() {
        return INSTANCE;
    }

    private Configuration() {
    }

    public int getPort() {
        return Integer.parseInt(System.getProperty(PORT, String.valueOf(8080)));
    }
}
