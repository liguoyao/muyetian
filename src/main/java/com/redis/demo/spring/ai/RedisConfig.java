package com.redis.demo.spring.ai;

import io.lettuce.core.metrics.MicrometerCommandLatencyRecorder;
import io.lettuce.core.metrics.MicrometerOptions;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
public class RedisConfig {

    @Bean
    public ClientResources clientResources(MeterRegistry meterRegistry) {

        return DefaultClientResources.builder()
                .commandLatencyRecorder(
                        new MicrometerCommandLatencyRecorder(
                                meterRegistry,
                                MicrometerOptions.builder()
                                        .histogram(false)
                                        .build()
                        )
                )
                .build();
    }

    @Bean
    public LettuceClientConfigurationBuilderCustomizer lettuceCustomizer(
            ClientResources clientResources) {

        return builder -> builder.clientResources(clientResources);
    }

}
