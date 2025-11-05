package com.example.travel.service;

import com.example.travel.entity.TravelPlanRecord;
import com.example.travel.repository.TravelPlanRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TravelPlanRecordService {

    private final TravelPlanRecordRepository travelPlanRecordRepository;

    @Autowired
    public TravelPlanRecordService(TravelPlanRecordRepository travelPlanRecordRepository) {
        this.travelPlanRecordRepository = travelPlanRecordRepository;
    }

    /**
     * 创建记录
     * @param record 记录对象
     * @return 保存后的记录
     */
    public TravelPlanRecord createRecord(TravelPlanRecord record) {
        return travelPlanRecordRepository.save(record);
    }

    /**
     * 根据ID获取记录
     * @param id 记录ID
     * @return 对应的记录
     */
    public TravelPlanRecord getRecordById(Long id) {
        return travelPlanRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("记录未找到，ID: " + id));
    }

    /**
     * 根据旅行计划ID获取所有记录
     * @param travelPlanId 旅行计划ID
     * @return 记录列表
     */
    public List<TravelPlanRecord> getRecordsByTravelPlanId(Long travelPlanId) {
        return travelPlanRecordRepository.findByTravelPlanId(travelPlanId);
    }

    /**
     * 更新记录
     * @param id 记录ID
     * @param updatedRecord 更新后的记录对象
     * @return 更新后的记录
     */
    public TravelPlanRecord updateRecord(Long id, TravelPlanRecord updatedRecord) {
        TravelPlanRecord existingRecord = travelPlanRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("记录未找到，ID: " + id));

        existingRecord.setContent(updatedRecord.getContent());
        return travelPlanRecordRepository.save(existingRecord);
    }

    /**
     * 删除记录
     * @param id 记录ID
     */
    public void deleteRecord(Long id) {
        travelPlanRecordRepository.deleteById(id);
    }
}