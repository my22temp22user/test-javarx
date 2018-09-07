package com.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.function.Consumer;

public class JsonConsumer implements IConsumer {

    private final Logger logger = Logger.getLogger(getClass());
    private final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public Consumer<String> onEvent() {
        return line -> {
            try {
                Map<String, String> res = objectMapper.readValue(line, Map.class);

                String eventType = res.get("event_type");

                Metrics.getInstance().receivedEventType(eventType);

                String data = res.get("data");
                String[] words = data.split(" ");
                for (String word : words) {
                    Metrics.getInstance().receivedWordInData(word);
                }

                if (logger.isInfoEnabled()) {
                    logger.info("eventTypeCounts: "+Metrics.getInstance().getEventTypeCounter());
                    logger.info("dataCounts: "+Metrics.getInstance().getWordsInDataCounter());
                }
            } catch (Exception ex) {
                if (logger.isInfoEnabled()) {
                    logger.info("failed to parse "+line);
                }
            }
        };
    }
}
