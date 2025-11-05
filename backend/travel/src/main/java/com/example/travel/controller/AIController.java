package com.example.travel.controller;
 
import com.example.travel.dto.DeeseekRequest;
import com.example.travel.entity.TravelPlan;
import com.example.travel.service.TravelPlanService;
import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.web.bind.annotation.*;
 
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
 
 
@RestController
public class AIController {
 
    private final Gson gson = new Gson();
    private final TravelPlanService travelPlanService;

    public AIController(TravelPlanService travelPlanService) {
        this.travelPlanService = travelPlanService;
    }

    @PostMapping("travelPlan")
    public TravelPlan askTravelPlan(@RequestBody String question) throws IOException, UnirestException {

        Unirest.setTimeouts(0, 0);

        //DeeseekRequest: 自己的实体类名称
        List<DeeseekRequest.Message> messages = new ArrayList<>();
        // 给 deepSeek 一个角色，并添加明确的 prompt 限制输出格式
        messages.add(DeeseekRequest.Message.builder()
                .role("system")
                .content("你是一个旅行规划师，接下来你会收到一些用户的旅行要求，如目的地，人数，预算等等。请生成一个包含以下内容的旅行计划：\n"
                        + "1. 交通: 描述旅行中的交通安排。\n"
                        + "2. 住宿: 描述旅行中的住宿安排。\n"
                        + "3. 景点: 列出旅行中的主要景点。\n"
                        + "4. 餐厅: 推荐旅行中的餐厅。\n"
                        + "请确保输出的格式清晰，并按照上述顺序组织内容。")
                .build());

        // question：说你自己想说的话
        messages.add(DeeseekRequest.Message.builder().role("user").content(question).build());

        DeeseekRequest requestBody = DeeseekRequest.builder()
                .model("deepseek-chat")
                .messages(messages)
                .build();
        // TODO: 设置自己的key
        HttpResponse<String> response = Unirest.post("https://api.deepseek.com/chat/completions")
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + "自己的key")
                .body(gson.toJson(requestBody))
                .asString();

        // 解析和格式化大语言模型的结果
        String aiGeneratedResult = response.getBody();

        // 调用 TravelPlanService 的方法创建并保存旅行计划
        TravelPlan travelPlan = travelPlanService.createAndSaveTravelPlan(aiGeneratedResult);

        // 返回旅行计划对象
        return travelPlan;
    }

    @PostMapping("/api/analyze-budget")
    public String analyzeBudget(@RequestParam Long travelPlanId) throws IOException, UnirestException {
        // 获取 TravelPlan 对象
        TravelPlan travelPlan = travelPlanService.findById(travelPlanId);

        // 构造 LLM 的输入
        List<DeeseekRequest.Message> messages = new ArrayList<>();
        messages.add(DeeseekRequest.Message.builder()
                .role("system")
                .content("你是一个预算分析师，请根据以下旅行计划生成预算分析：\n"
                        + "目的地: " + travelPlan.getDestination() + "\n"
                        + "出发地: " + travelPlan.getOrigin() + "\n"
                        + "人数: " + travelPlan.getPeopleNum() + "\n"
                        + "预算: " + travelPlan.getBudget() + "\n"
                        + "开始日期: " + travelPlan.getStartDate() + "\n"
                        + "结束日期: " + travelPlan.getEndDate() + "\n"
                        + "细节(包括景点、餐饮等): " + travelPlan.getDetails() + "\n"
                        + "请详细列出交通、住宿、餐饮和景点的预算分配。")
                .build());

        DeeseekRequest requestBody = DeeseekRequest.builder()
                .model("deepseek-chat")
                .messages(messages)
                .build();

        // 调用 LLM API
        HttpResponse<String> response = Unirest.post("https://api.deepseek.com/chat/completions")
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + "自己的key")
                .body(gson.toJson(requestBody))
                .asString();

        // 返回 LLM 的预测开销结果
        return response.getBody();
    }
}