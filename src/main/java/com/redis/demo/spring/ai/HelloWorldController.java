package com.redis.demo.spring.ai;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {

    private final HelloWorldService helloWorldService;

    public HelloWorldController(HelloWorldService helloWorldService) {
        this.helloWorldService = helloWorldService;
    }

    @PostMapping("/hello-world/start")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void start() {
        helloWorldService.start();
    }
}
