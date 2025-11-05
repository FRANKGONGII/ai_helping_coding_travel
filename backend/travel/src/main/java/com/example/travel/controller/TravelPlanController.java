package com.example.travel.controller;

import com.example.travel.entity.TravelPlan;
import com.example.travel.service.TravelPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/travel-plans")
public class TravelPlanController {

    private final TravelPlanService travelPlanService;

    @Autowired
    public TravelPlanController(TravelPlanService travelPlanService) {
        this.travelPlanService = travelPlanService;
    }

    /**
     * 创建旅行计划
     * @param travelPlan 旅行计划对象
     * @param aiGeneratedResult AI 生成的原始行程数据
     * @return 创建的旅行计划
     */
    @PostMapping
    public ResponseEntity<TravelPlan> createTravelPlan(@RequestBody TravelPlan travelPlan,
                                                       @RequestParam String aiGeneratedResult) {
        TravelPlan createdPlan = travelPlanService.createTravelPlan(travelPlan, aiGeneratedResult);
        return ResponseEntity.ok(createdPlan);
    }
}