package com.example.travel.service;

import com.example.travel.entity.TravelPlan;
import com.example.travel.repository.TravelPlanRepository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TravelPlanService {

    private final TravelPlanRepository travelPlanRepository;
    private final AIResultFormatter aiResultFormatter;

    @Autowired
    public TravelPlanService(TravelPlanRepository travelPlanRepository, AIResultFormatter aiResultFormatter) {
        this.travelPlanRepository = travelPlanRepository;
        this.aiResultFormatter = aiResultFormatter;
    }

    /**
     * 创建旅行计划
     * @param travelPlan 旅行计划对象
     * @param aiGeneratedResult AI 生成的原始行程数据
     * @return 保存后的旅行计划
     */
    public TravelPlan createTravelPlan(TravelPlan travelPlan, String aiGeneratedResult) {
        // 格式化 AI 生成的结果
        String formattedDetails = aiResultFormatter.formatAIResult(aiGeneratedResult).toString();
        travelPlan.setDetails(formattedDetails);

        // 保存旅行计划到数据库
        return travelPlanRepository.save(travelPlan);
    }

    /**
     * 根据 AI 生成的结果创建并保存旅行计划
     * @param aiGeneratedResult AI 生成的原始行程数据
     * @return 保存后的旅行计划
     */
    public TravelPlan createAndSaveTravelPlan(String aiGeneratedResult) {
        // 格式化 AI 生成的结果
        String formattedDetails = aiResultFormatter.formatAIResult(aiGeneratedResult).toString();

        // 创建旅行计划对象
        TravelPlan travelPlan = new TravelPlan();
        travelPlan.setDetails(formattedDetails);

        // 保存旅行计划到数据库
        return travelPlanRepository.save(travelPlan);
    }

    /**
     * 按出发点和目的地查询旅行计划
     * @param origin 出发点
     * @param destination 目的地
     * @return 符合条件的旅行计划列表
     */
    public List<TravelPlan> findByOriginAndDestination(String origin, String destination) {
        return travelPlanRepository.findByOriginAndDestination(origin, destination);
    }

    /**
     * 按时间范围查询旅行计划
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 符合条件的旅行计划列表
     */
    public List<TravelPlan> findByStartDateBetween(LocalDate startDate, LocalDate endDate) {
        return travelPlanRepository.findByStartDateBetween(startDate, endDate);
    }

    /**
     * 根据用户ID查询所有旅行计划
     * @param userId 用户ID
     * @return 用户的所有旅行计划列表
     */
    public List<TravelPlan> findByUserId(Long userId) {
        return travelPlanRepository.findByUserId(userId);
    }

    /**
     * 删除旅行计划
     * @param travelPlanId 旅行计划ID
     */
    public void deleteTravelPlan(Long travelPlanId) {
        travelPlanRepository.deleteById(travelPlanId);
    }

    /**
     * 修改旅行计划
     * @param travelPlanId 旅行计划ID
     * @param updatedTravelPlan 包含更新信息的旅行计划对象
     * @return 更新后的旅行计划
     */
    public TravelPlan updateTravelPlan(Long travelPlanId, TravelPlan updatedTravelPlan) {
        // 查找现有的旅行计划
        TravelPlan existingTravelPlan = travelPlanRepository.findById(travelPlanId)
                .orElseThrow(() -> new IllegalArgumentException("旅行计划未找到，ID: " + travelPlanId));

        // 更新旅行计划的字段
        existingTravelPlan.setDestination(updatedTravelPlan.getDestination());
        existingTravelPlan.setStartDate(updatedTravelPlan.getStartDate());
        existingTravelPlan.setEndDate(updatedTravelPlan.getEndDate());
        existingTravelPlan.setBudget(updatedTravelPlan.getBudget());
        existingTravelPlan.setPreferences(updatedTravelPlan.getPreferences());
        existingTravelPlan.setDetails(updatedTravelPlan.getDetails());
        existingTravelPlan.setOrigin(updatedTravelPlan.getOrigin());

        // 保存更新后的旅行计划
        return travelPlanRepository.save(existingTravelPlan);
    }

    /**
     * 根据 ID 查找旅行计划
     * @param travelPlanId 旅行计划 ID
     * @return 对应的旅行计划对象
     */
    public TravelPlan findById(Long travelPlanId) {
        return travelPlanRepository.findById(travelPlanId)
                .orElseThrow(() -> new IllegalArgumentException("旅行计划未找到，ID: " + travelPlanId));
    }
}