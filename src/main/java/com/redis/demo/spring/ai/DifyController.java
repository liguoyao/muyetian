package com.redis.demo.spring.ai;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import okhttp3.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/dify")
public class DifyController {

    private static String url = "https://api.dify.ai/v1";
    private static String key = "app-J3lDnHOY5NoTnaiezsNugzQ0";

    @GetMapping("/start")
    public static String start() {
        OkHttpClient client = new OkHttpClient();
        String json = """
                {
                    "inputs": {
                        "input_data": "你是一个数学老师，简洁回答数学问题，得到答案后将结果用中文汉字格式输出"
                    },
                    "query":"3+1的结果是什么，用中文汉字展示结果",
                    "user":"1234"
                }
                """;
        RequestBody requestBody = RequestBody.create(json, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .post(requestBody)
                .addHeader("Authorization", "Bearer " + key)
                .url(url + "/chat-messages")
                .build();
        String result = "";
        try (Response response = client.newCall(request).execute()) {
            if (response.body() != null) {
                result = response.body().string();
                JSONObject jsonObject = JSONObject.parseObject(result);
                result = jsonObject.getString("answer");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println(start());
    }
}
