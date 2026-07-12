package com.redis.demo.spring.ai;

import java.util.List;
import java.util.UUID;

import com.alibaba.fastjson.JSONObject;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.Generation;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

@RestController
public class RagController {

    private static final Logger log = LoggerFactory.getLogger(RagController.class);
    private final RagService ragService;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObservationRegistry registry;

    @Resource(name = "myMongoTemplate")
    private MongoTemplate mongoTemplate;

	public RagController(RagService ragService, StringRedisTemplate stringRedisTemplate, ObservationRegistry registry) {
		this.ragService = ragService;
        this.stringRedisTemplate = stringRedisTemplate;
        this.registry = registry;
	}

    @GetMapping("/mongo")
    public String mongo() {
        Observation.createNotStarted(
                        "mongo-module",
                        registry
                )
                .lowCardinalityKeyValue(
                        "module",
                        "mongo"
                )
                .observe(() -> {
                    JSONObject jsonObject = new JSONObject();
                    long millis = System.currentTimeMillis();
                    jsonObject.put("key1" + millis, "value1" + millis);
                    jsonObject.put("key2" + millis, "value2" + millis);
                    mongoTemplate.save(jsonObject, "col_first");
                    List<Object> colFirst = mongoTemplate.findAll(Object.class, "col_first");
                    System.err.println(colFirst);
                });
        return "";
    }

    @Scheduled(fixedDelay = 3000)
    public void redisTask() {
        stringRedisTemplate.hasKey("1111");
        stringRedisTemplate.opsForValue().set("1111", "1111");
        stringRedisTemplate.opsForValue().get("1111");
    }

    @GetMapping("/log")
    public String log() {
        log.info("11111111111");
        Boolean b = stringRedisTemplate.hasKey("1111");
        log.warn(String.valueOf(b));

        List<Object> colFirst = mongoTemplate.findAll(Object.class, "col_first");
        System.out.println(colFirst);

        return "success";
    }

	@PostMapping("/chat/startChat")
	@ResponseBody
	public Message startChat() {
		return Message.of(UUID.randomUUID().toString());
	}

	//tag::chatMessage[]
	@PostMapping("/chat/{chatId}")
	@ResponseBody
	public Message chatMessage(@PathVariable("chatId") String chatId, @RequestBody Prompt prompt) {
		// Extract user prompt from the body and pass it to the RagService
		Generation generation = ragService.retrieve(prompt.getPrompt());
		// Reply with the generated message
		return Message.of(generation.getOutput().getContent());
	}
	//end::chatMessage[]

	@PostMapping("/documents/upload")
	@ResponseBody
	public String uploadDocument(String doc) {
		return "Document upload not supported";
	}

	public static class Message {

		private String message;

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public static Message of(String message) {
			Message response = new Message();
			response.setMessage(message);
			return response;
		}

	}

	public static class Prompt {

		private String prompt;

		public String getPrompt() {
			return prompt;
		}

		public void setPrompt(String prompt) {
			this.prompt = prompt;
		}

	}

}
