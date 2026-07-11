package com.redis.demo.spring.ai;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.ExecutorServiceMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.metrics.cache.RedisCacheMetrics;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableScheduling
public class RagApplication {

    private static final Logger log = LoggerFactory.getLogger(RagApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(RagApplication.class, args);
        log.info("dadadada");
    }

    @Bean
    public ThreadPoolExecutor orderExecutor(
            MeterRegistry meterRegistry) {

        ThreadPoolExecutor executor =
                new ThreadPoolExecutor(
                        20,
                        100,
                        60,
                        TimeUnit.SECONDS,
                        new LinkedBlockingQueue<>(1000)
                );

        ExecutorServiceMetrics.monitor(
                meterRegistry,
                executor,
                "orderExecutor"
        );


        return executor;
    }

}
