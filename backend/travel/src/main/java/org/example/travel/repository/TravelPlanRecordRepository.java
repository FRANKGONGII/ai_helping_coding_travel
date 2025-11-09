package org.example.travel.repository;

import org.example.travel.entity.TravelPlanRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TravelPlanRecordRepository extends JpaRepository<TravelPlanRecord, Long> {

    /**
     * 根据旅行计划ID查询所有记录
     * @param travelPlanId 旅行计划ID
     * @return 记录列表
     */
    List<TravelPlanRecord> findByTravelPlanId(Long travelPlanId);
}