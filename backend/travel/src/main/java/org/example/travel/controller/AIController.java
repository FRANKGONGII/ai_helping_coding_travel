package org.example.travel.controller;
 
import org.example.travel.dto.DeeseekRequest;
import org.example.travel.entity.TravelPlan;
import org.example.travel.service.TravelPlanService;
import com.google.gson.Gson;
import org.springframework.web.bind.annotation.*;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.Map;
import java.util.HashMap;


@RestController
@RequestMapping("/api/ai")
public class AIController {

    private final Gson gson = new Gson();
    private final TravelPlanService travelPlanService;
    private final OkHttpClient httpClient; // 使用 OkHttpClient

    @Autowired
    public AIController(TravelPlanService travelPlanService) {
        this.travelPlanService = travelPlanService;
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
    }

    @PostMapping("/travelPlan")
    public String askTravelPlan(@org.springframework.web.bind.annotation.RequestBody String question) throws IOException {
        System.out.println(question);

        String apiKey = System.getenv("DEEPSEEK_API_KEY"); // 从环境变量获取 API Key
//        if (apiKey == null || apiKey.isEmpty()) {
//            throw new IllegalArgumentException("DEEPSEEK_API_KEY environment variable not set.");
//        }

        // apiKey = "sk-ac1de879f40a4629a2f5f38653d3a5e3";
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalArgumentException("DEEPSEEK_API_KEY environment variable not set.");
        }

        Map<String, Object> messageSystem = new HashMap<>();
        messageSystem.put("role", "system");
        messageSystem.put("content", "你是一个旅行规划师，接下来你会收到一些用户的旅行要求，如目的地，人数，预算等等。请生成一个包含以下内容的旅行计划：\n"
                        + "1. 交通: 描述旅行中的交通安排。\n"
                        + "2. 住宿: 描述旅行中的住宿安排。\n"
                        + "3. 景点: 列出旅行中的主要景点。\n"
                        + "4. 餐厅: 推荐旅行中的餐厅。\n"
                        + "请确保以这个格式输出！！！对于1. 交通这样的标题，不要加粗！！！输出的格式清晰，并按照上述顺序组织内容。");

        Map<String, Object> messageUser = new HashMap<>();
        messageUser.put("role", "user");
        messageUser.put("content", question);

        List<Map<String, Object>> messages = new ArrayList<>();
        messages.add(messageSystem);
        messages.add(messageUser);

        Map<String, Object> requestBodyMap = new HashMap<>();
        requestBodyMap.put("model", "deepseek-chat");
        requestBodyMap.put("messages", messages);
        requestBodyMap.put("stream", false);
        requestBodyMap.put("temperature", 0.7);
        requestBodyMap.put("max_tokens", 4096);
        requestBodyMap.put("frequency_penalty", 0);
        requestBodyMap.put("presence_penalty", 0);
        requestBodyMap.put("response_format", new HashMap<String, String>() {{ put("type", "text"); }});
        requestBodyMap.put("stop", null);
        requestBodyMap.put("stream_options", null);
        requestBodyMap.put("top_p", 1);
        requestBodyMap.put("tools", null);
        requestBodyMap.put("tool_choice", "none");
        requestBodyMap.put("logprobs", false);
        requestBodyMap.put("top_logprobs", null);

        String jsonBody = gson.toJson(requestBodyMap);

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonBody);
        Request request = new Request.Builder()
                .url("https://api.deepseek.com/chat/completions")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            String responseBody = response.body().string();
            Map<String, Object> responseMap = gson.fromJson(responseBody, Map.class);
            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseMap.get("choices");
            if (choices != null && !choices.isEmpty()) {
                Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                System.out.println((String) message.get("content"));
                return (String) message.get("content");
            }
            return "Error: No content received from DeepSeek API.";
        }
    }

    @PostMapping("/analyze-budget")
    public String analyzeBudget(@RequestParam Long travelPlanId) throws IOException {
        System.out.println("要检查的id: " + travelPlanId);
        TravelPlan travelPlan = travelPlanService.findById(travelPlanId);

        String apiKey = System.getenv("DEEPSEEK_API_KEY");
//        if (apiKey == null || apiKey.isEmpty()) {
//            throw new IllegalArgumentException("DEEPSEEK_API_KEY environment variable not set.");
//        }
        // apiKey = "sk-ac1de879f40a4629a2f5f38653d3a5e3";
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalArgumentException("DEEPSEEK_API_KEY environment variable not set.");
        }

        Map<String, Object> messageSystem = new HashMap<>();
        messageSystem.put("role", "system");
        messageSystem.put("content", "你是一个预算分析师，请根据以下旅行计划生成预算分析：\n"
                        + "目的地: " + travelPlan.getDestination() + "\n"
                        + "出发地: " + travelPlan.getOrigin() + "\n"
                        + "人数: " + travelPlan.getPeopleNum() + "\n"
                        + "预算: " + travelPlan.getBudget() + "\n"
                        + "开始日期: " + travelPlan.getStartDate() + "\n"
                        + "结束日期: " + travelPlan.getEndDate() + "\n"
                        + "细节(包括景点、餐饮等): " + travelPlan.getDetails() + "\n"
                        + "请详细列出交通、住宿、餐饮和景点的预算分配。");

        List<Map<String, Object>> messages = new ArrayList<>();
        messages.add(messageSystem);

        Map<String, Object> requestBodyMap = new HashMap<>();
        requestBodyMap.put("model", "deepseek-chat");
        requestBodyMap.put("messages", messages);
        requestBodyMap.put("stream", false);
        requestBodyMap.put("temperature", 1);
        requestBodyMap.put("max_tokens", 4096);
        requestBodyMap.put("frequency_penalty", 0);
        requestBodyMap.put("presence_penalty", 0);
        requestBodyMap.put("response_format", new HashMap<String, String>() {{ put("type", "text"); }});
        requestBodyMap.put("stop", null);
        requestBodyMap.put("stream_options", null);
        requestBodyMap.put("top_p", 1);
        requestBodyMap.put("tools", null);
        requestBodyMap.put("tool_choice", "none");
        requestBodyMap.put("logprobs", false);
        requestBodyMap.put("top_logprobs", null);

        String jsonBody = gson.toJson(requestBodyMap);

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonBody);
        Request request = new Request.Builder()
                .url("https://api.deepseek.com/chat/completions")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            String responseBody = response.body().string();
            Map<String, Object> responseMap = gson.fromJson(responseBody, Map.class);
            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseMap.get("choices");
            if (choices != null && !choices.isEmpty()) {
                Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                return (String) message.get("content");
            }
            return "Error: No content received from DeepSeek API.";
        }
    }
}