package com.redis.demo.spring.ai;

import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
public class HelloWorldService {

    private static final Logger log = LoggerFactory.getLogger(HelloWorldService.class);
    private static final long INTERVAL_SECONDS = 10;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(
            task -> new Thread(task, "hello-world-scheduler"));

    private ScheduledFuture<?> loggingTask;

    public synchronized void start() {
        if (loggingTask == null && !scheduler.isShutdown()) {
            loggingTask = scheduler.scheduleAtFixedRate(
                    () -> log.info("Hello World"), INTERVAL_SECONDS, INTERVAL_SECONDS, TimeUnit.SECONDS);
        }
    }

    @PreDestroy
    synchronized void shutdown() {
        if (loggingTask != null) {
            loggingTask.cancel(false);
        }
        scheduler.shutdown();
    }
}
