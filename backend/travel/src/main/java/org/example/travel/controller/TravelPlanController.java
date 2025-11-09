package org.example.travel.controller;

import org.example.travel.entity.TravelPlan;
import org.example.travel.entity.TravelPlanRecord;
import org.example.travel.service.TravelPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.example.travel.service.TravelPlanRecordService;
import java.util.List;

@RestController
@RequestMapping("/api/travelPlan")
public class TravelPlanController {

    private final TravelPlanService travelPlanService;
    private final TravelPlanRecordService travelPlanRecordService;

    @Autowired
    public TravelPlanController(TravelPlanService travelPlanService, TravelPlanRecordService travelPlanRecordService) {
        this.travelPlanService = travelPlanService;
        this.travelPlanRecordService = travelPlanRecordService;
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
        TravelPlan createdPlan = travelPlanService.createTravelPlan(travelPlan);
        return ResponseEntity.ok(createdPlan);
    }

    @PostMapping("/save")
    public ResponseEntity<TravelPlan> saveTravelPlan(@RequestBody TravelPlan travelPlan) {
        System.out.println(travelPlan.getPeopleNum());
        TravelPlan savedPlan = travelPlanService.createTravelPlan(travelPlan);
        return ResponseEntity.ok(savedPlan);
    }

    /**
     * 根据用户ID查询所有旅行计划
     * @param userId 用户ID
     * @return 用户的所有旅行计划列表
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TravelPlan>> getTravelPlansByUserId(@PathVariable Long userId) {
        List<TravelPlan> travelPlans = travelPlanService.findByUserId(userId);
        return ResponseEntity.ok(travelPlans);
    }

    // TravelPlanRecord API

    /**
     * 创建旅行开销记录
     * @param travelPlanRecord 旅行开销记录对象
     * @return 创建的旅行开销记录
     */
    @PostMapping("/record")
    public ResponseEntity<TravelPlanRecord> createTravelPlanRecord(@RequestBody TravelPlanRecord travelPlanRecord) {
        TravelPlanRecord createdRecord = travelPlanRecordService.createRecord(travelPlanRecord);
        return ResponseEntity.ok(createdRecord);
    }

    /**
     * 根据旅行计划ID查询所有旅行开销记录
     * @param travelPlanId 旅行计划ID
     * @return 旅行开销记录列表
     */
    @GetMapping("/record/plan/{travelPlanId}")
    public ResponseEntity<List<TravelPlanRecord>> getTravelPlanRecordsByTravelPlanId(@PathVariable Long travelPlanId) {
        List<TravelPlanRecord> records = travelPlanRecordService.getRecordsByTravelPlanId(travelPlanId);
        return ResponseEntity.ok(records);
    }

    /**
     * 更新旅行开销记录
     * @param id 记录ID
     * @param travelPlanRecord 更新后的旅行开销记录对象
     * @return 更新后的旅行开销记录
     */
    @PutMapping("/record/{id}")
    public ResponseEntity<TravelPlanRecord> updateTravelPlanRecord(@PathVariable Long id, @RequestBody TravelPlanRecord travelPlanRecord) {
        TravelPlanRecord updatedRecord = travelPlanRecordService.updateRecord(id, travelPlanRecord);
        return ResponseEntity.ok(updatedRecord);
    }

    /**
     * 删除旅行开销记录
     * @param id 记录ID
     * @return 无内容响应
     */
    @DeleteMapping("/record/{id}")
    public ResponseEntity<Void> deleteTravelPlanRecord(@PathVariable Long id) {
        travelPlanRecordService.deleteRecord(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 删除旅行计划
     * @param id 旅行计划ID
     * @return 无内容响应
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTravelPlan(@PathVariable Long id) {
        travelPlanService.deleteTravelPlan(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 更新旅行计划
     * @param id 旅行计划ID
     * @param travelPlan 更新后的旅行计划对象
     * @return 更新后的旅行计划
     */
    @PutMapping("/{id}")
    public ResponseEntity<TravelPlan> updateTravelPlan(@PathVariable Long id, @RequestBody TravelPlan travelPlan) {
        TravelPlan updatedPlan = travelPlanService.updateTravelPlan(id, travelPlan);
        return ResponseEntity.ok(updatedPlan);
    }
}