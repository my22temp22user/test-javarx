package com.test;

import com.google.common.io.ByteStreams;
import com.sun.javafx.PlatformUtil;
import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executors;

public class JsonProducer {

    private final Logger logger = Logger.getLogger(getClass());

    private final List<IConsumer> consumers = new ArrayList<>();

    public void addSubscriber(IConsumer consumer) {
        logger.info("added consumer");

        consumers.add(consumer);
    }

    public void start() {

        logger.info("starting produce events");

        PublishSubject<String> source = PublishSubject.create();

        Observable<String> listObservable = source
                .observeOn(Schedulers.computation());

        for (IConsumer consumer : consumers) {
            logger.info("subscribing to events");

            listObservable.subscribe(line -> consumer.actionOnEvent().accept(line));
        }

        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                produceEvents(source);
            } catch (IOException ioEx) {
                logger.error("failed to read events stream", ioEx);
            }
        });
    }

    private void produceEvents(PublishSubject<String> source) throws IOException {

        File file = File.createTempFile("generator", ""); // assuming mac os
        if (!file.setExecutable(true)) {
            throw new IllegalStateException("failed to grant execution permissions for file " + file.getAbsolutePath());
        }

        file.deleteOnExit();

        if (logger.isInfoEnabled()) {
            logger.info("created temp file " + file.getAbsolutePath());
        }

        try (InputStream in = new ClassPathResource(getGeneratorResourcePath()).getInputStream();
             OutputStream out = new FileOutputStream(file)) {
            ByteStreams.copy(in, out);
        }

        Process proc = Runtime.getRuntime().exec(file.getAbsolutePath());

        try (InputStream in = proc.getInputStream();
             Scanner scanner = new Scanner(in)) {

            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                if (logger.isInfoEnabled()) {
                    logger.info("generated: " + line);
                }
                source.onNext(line);
            }
        }
    }

    private String getGeneratorResourcePath() {
        return Paths.get("generator",getGeneratorFileName()).toString();
    }

    private String getGeneratorFileName() {
        if (PlatformUtil.isMac()) {
            return "generator-macosx-amd64";
        } else if (PlatformUtil.isLinux()) {
            return "generator-linux-amd64";
        } else if (PlatformUtil.isWindows()) {
            return "generator-windows-amd64.exe";
        } else {
            throw new UnsupportedOperationException("unsupported operation system");
        }
    }
}
