package com.redis.demo.spring.ai;

import java.util.Map;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.autoconfigure.vectorstore.redis.RedisVectorStoreProperties;
import org.springframework.ai.reader.JsonReader;
import org.springframework.ai.vectorstore.RedisVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class RagDataLoader implements ApplicationRunner {

	private static final Logger logger = LoggerFactory.getLogger(RagDataLoader.class);

	private static final String[] KEYS = { "name", "abv", "ibu", "description" };

	@Value("classpath:/data/beers.json.gz")
	private Resource data;

	private final RedisVectorStore vectorStore;

	private final RedisVectorStoreProperties properties;

	public RagDataLoader(RedisVectorStore vectorStore, RedisVectorStoreProperties properties) {
		this.vectorStore = vectorStore;
		this.properties = properties;
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		Map<String, Object> indexInfo = vectorStore.getJedis().ftInfo(properties.getIndex());
		int numDocs = Integer.parseInt(String.valueOf(indexInfo.getOrDefault("num_docs", "0")));
		if (numDocs > -1) {
			logger.info("Embeddings already loaded. Skipping");
			return;
		}
		Resource file = data;
		if (data.getFilename().endsWith(".gz")) {
			GZIPInputStream inputStream = new GZIPInputStream(data.getInputStream());
			file = new InputStreamResource(inputStream, "beers.json.gz");
		}
		logger.info("Creating Embeddings...");
		// tag::loader[]
		// Create a JSON reader with fields relevant to our use case
		JsonReader loader = new JsonReader(file, KEYS);
		// Use the autowired VectorStore to insert the documents into Redis
		vectorStore.add(loader.get());
		// end::loader[]
		logger.info("Embeddings created.");
	}

    static class DelayTask implements Delayed {

        public long expireTime;

        public DelayTask(Integer c) {
            this.expireTime =
                    System.currentTimeMillis()
                            + c * 1000;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            long remain =
                    expireTime - System.currentTimeMillis();

            return unit.convert(remain,
                    TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(Delayed o) {
            DelayTask task =
                    (DelayTask) o;

            return Long.compare(
                    this.expireTime,
                    task.expireTime
            );
        }
    }

    public static void main(String[] args) throws InterruptedException {
        DelayQueue<DelayTask> delayQueue = new DelayQueue<>();
        delayQueue.put(new DelayTask(3));
        delayQueue.put(new DelayTask(6));
        delayQueue.put(new DelayTask(9));
        while (true) {

            DelayTask task =
                    delayQueue.take();

            System.out.println(
                    "取消超时订单："
                            + " 时间:"
                            + System.currentTimeMillis()
            );
        }
    }

}
