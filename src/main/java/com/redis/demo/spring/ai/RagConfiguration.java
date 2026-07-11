package com.redis.demo.spring.ai;

import java.time.Duration;
import java.util.concurrent.*;

import io.micrometer.core.instrument.binder.jvm.ExecutorServiceMetrics;
import org.springframework.ai.autoconfigure.vectorstore.redis.RedisVectorStoreProperties;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.openai.OpenAiEmbeddingClient;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.ai.vectorstore.RedisVectorStore;
import org.springframework.ai.vectorstore.RedisVectorStore.RedisVectorStoreConfig;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import redis.clients.jedis.Connection;
import redis.clients.jedis.util.Pool;

@Configuration
public class RagConfiguration {

    @Bean
    OpenAiEmbeddingClient openAiEmbeddingClient() {
        OpenAiApi openAiApi = new OpenAiApi(
                "https://dashscope.aliyuncs.com/compatible-mode",
                "sk-8a15c57ab4c34fe0bd9bbfed2d1596a5");
        OpenAiEmbeddingOptions options = OpenAiEmbeddingOptions.builder()
                .withModel("text-embedding-v3")
                .build();
        return new OpenAiEmbeddingClient(openAiApi, MetadataMode.EMBED, options, RetryUtils.DEFAULT_RETRY_TEMPLATE);
    }

    @Bean
    VectorStore vectorStore(OpenAiEmbeddingClient embeddingClient, RedisVectorStoreProperties properties) {
        var config = RedisVectorStoreConfig.builder().withURI(properties.getUri()).withIndexName(properties.getIndex())
                .withPrefix(properties.getPrefix()).build();
        RedisVectorStore vectorStore = new RedisVectorStore(config, embeddingClient);
        configureRedisConnectionPool(vectorStore);
        vectorStore.afterPropertiesSet();
        return vectorStore;
    }

    private void configureRedisConnectionPool(RedisVectorStore vectorStore) {
        Pool<Connection> pool = vectorStore.getJedis().getPool();
        pool.setTestOnBorrow(true);
        pool.setTestWhileIdle(true);
        pool.setTimeBetweenEvictionRuns(Duration.ofMinutes(1));
        pool.setMinEvictableIdleTime(Duration.ofMinutes(2));
        pool.setNumTestsPerEvictionRun(-1);
    }

    @Bean
    RagService ragService(ChatClient chatClient, VectorStore vectorStore) {
        return new RagService(chatClient, vectorStore);
    }

    public static void main(String[] args) {
        MonitorExecutor executor = new MonitorExecutor(
                        10,
                        20,
                        60,
                        TimeUnit.SECONDS,
                        new ArrayBlockingQueue<>(1000),
                        Executors.defaultThreadFactory(),
                        new MonitorRejectHandler()
                );
        executor.submit(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.err.println("22222222222");
        });
        executor.submit(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.err.println("22222222222");
        });
        System.out.println("activeCount: " + executor.getActiveCount());
        System.out.println("completedTasks: " + executor.getCompletedTaskCount());
        System.out.println("corePoolSize: " + executor.getCorePoolSize());
        System.out.println("PoolSize: " + executor.getPoolSize());
        System.out.println("queueSize: " + executor.getQueue().size());
//        try {
//            Thread.sleep(70000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
        System.out.println("activeCount: " + executor.getActiveCount());
        System.out.println("completedTasks: " + executor.getCompletedTaskCount());
        System.out.println("corePoolSize: " + executor.getCorePoolSize());
        System.out.println("PoolSize: " + executor.getPoolSize());
        System.out.println("queueSize: " + executor.getQueue().size());
        MonitorRejectHandler handler = (MonitorRejectHandler) executor.getRejectedExecutionHandler();
        long rejectCount = handler.getRejectCount();
        System.out.println(rejectCount);


//        ExecutorServiceMetrics.monitor()
    }
}
