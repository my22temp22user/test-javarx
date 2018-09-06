package com.test;

import com.google.common.io.CharStreams;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

import static com.google.common.util.concurrent.Uninterruptibles.sleepUninterruptibly;
import static com.test.Constants.PORT;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class ServerIT {

    private final Logger logger = Logger.getLogger(getClass());
    private final Random random = new Random();
    private int port;


    @Before
    public void before() throws Throwable {
        RetryTemplate retryTemplate = new RetryTemplate();

        SimpleRetryPolicy policy = new SimpleRetryPolicy();
        policy.setMaxAttempts(3);
        retryTemplate.setRetryPolicy(policy);

        retryTemplate.execute((RetryCallback<Object, Throwable>) context -> {
            startServer();
            return null;
        });


        sleepUninterruptibly(3, SECONDS);
    }

    private void startServer() throws Exception {
        port = generatePort();
        System.setProperty(PORT, String.valueOf(port));
        Main.main(new String[0]);
    }

    @Test
    public void test() throws Exception {
        int eventTypeCount = Integer.parseInt(getEventTypeCount("baz", port));
        logger.info("eventTypeCount: " + eventTypeCount);
        assertTrue(eventTypeCount > 0);

        int dataWordCount = Integer.parseInt(getDataWordCount("amet", port));
        logger.info("dataWordCount: " + dataWordCount);
        assertTrue(dataWordCount > 0);
    }

    private String getEventTypeCount(String eventType, int port) throws IOException {
        return executeHttpGetRequest("/service/test/count/events/type/"+eventType, port);
    }

    private String getDataWordCount(String word, int port) throws IOException {
        return executeHttpGetRequest("/service/test/count/data/word/"+word, port);
    }

    private String executeHttpGetRequest(String path, int port) throws IOException {
        String url = "http://localhost:" + port + path;

        logger.info("executing http request: " + url);

        Request get = Request.Get(url);
        HttpResponse response = null;
        response = get.execute().returnResponse();

        assertEquals(200, response.getStatusLine().getStatusCode());

        return CharStreams.toString(new InputStreamReader(response.getEntity().getContent()));
    }

    private synchronized int generatePort() {
        int port = random.nextInt(65535 - 1024) + 1024;

        logger.info("generated port: " + port);

        return port;
    }
}
