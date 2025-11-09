package org.example.travel.controller;

import org.example.travel.entity.TravelPlanRecord;
import org.example.travel.service.TravelPlanRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/records")
public class TravelPlanRecordController {

    private final TravelPlanRecordService travelPlanRecordService;

    @Autowired
    public TravelPlanRecordController(TravelPlanRecordService travelPlanRecordService) {
        this.travelPlanRecordService = travelPlanRecordService;
    }

    /**
     * 创建记录
     * @param record 记录对象
     * @return 保存后的记录
     */
    @PostMapping
    public TravelPlanRecord createRecord(@RequestBody TravelPlanRecord record) {
        return travelPlanRecordService.createRecord(record);
    }

    /**
     * 根据ID获取记录
     * @param id 记录ID
     * @return 对应的记录
     */
    @GetMapping("/{id}")
    public TravelPlanRecord getRecordById(@PathVariable Long id) {
        return travelPlanRecordService.getRecordById(id);
    }

    /**
     * 根据旅行计划ID获取所有记录
     * @param travelPlanId 旅行计划ID
     * @return 记录列表
     */
    @GetMapping("/travel-plan/{travelPlanId}")
    public List<TravelPlanRecord> getRecordsByTravelPlanId(@PathVariable Long travelPlanId) {
        return travelPlanRecordService.getRecordsByTravelPlanId(travelPlanId);
    }

    /**
     * 更新记录
     * @param id 记录ID
     * @param updatedRecord 更新后的记录对象
     * @return 更新后的记录
     */
    @PutMapping("/{id}")
    public TravelPlanRecord updateRecord(@PathVariable Long id, @RequestBody TravelPlanRecord updatedRecord) {
        return travelPlanRecordService.updateRecord(id, updatedRecord);
    }

    /**
     * 删除记录
     * @param id 记录ID
     */
    @DeleteMapping("/{id}")
    public void deleteRecord(@PathVariable Long id) {
        travelPlanRecordService.deleteRecord(id);
    }
}