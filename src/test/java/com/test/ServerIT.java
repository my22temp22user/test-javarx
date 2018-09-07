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
        assertTrue(getEventTypeCount("baz") > 0);
        assertTrue(getDataWordCount("amet") > 0);

        assertEquals(0, getEventTypeCount("not_existing_event_type"));
        assertEquals(0, getDataWordCount("not_existing_word_in_data"));
    }

    private int getDataWordCount(String dataWord) throws IOException {
        int dataWordCount = getCount("/service/test/count/data/word/"+dataWord);

        logger.info("dataWord " + dataWord + ", count: " + dataWordCount);

        return dataWordCount;
    }

    private int getEventTypeCount(String eventType) throws IOException {
        int eventTypeCount = getCount("/service/test/count/events/type/" + eventType);

        logger.info("eventType " + eventType + ", count: " + eventTypeCount);

        return eventTypeCount;
    }

    private int getCount(String path) throws IOException {
        return Integer.parseInt(executeHttpGetRequest(path));
    }

    private String executeHttpGetRequest(String path) throws IOException {
        String url = "http://localhost:" + port + path;

        logger.info("executing http request: " + url);

        Request get = Request.Get(url);
        HttpResponse response = null;
        response = get.execute().returnResponse();

        assertEquals(200, response.getStatusLine().getStatusCode());

        return CharStreams.toString(new InputStreamReader(response.getEntity().getContent()));
    }

    private int generatePort() {
        int port = random.nextInt(65535 - 1024) + 1024;

        logger.info("generated port: " + port);

        return port;
    }
}
