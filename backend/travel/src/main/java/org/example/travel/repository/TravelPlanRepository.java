package org.example.travel.repository;

import org.example.travel.entity.TravelPlan;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TravelPlanRepository extends JpaRepository<TravelPlan, Long> {
    // 可以根据需要添加自定义查询方法
    List<TravelPlan> findByOriginAndDestination(String origin, String destination);

    List<TravelPlan> findByStartDateBetween(LocalDate startDate, LocalDate endDate);

    List<TravelPlan> findByUserId(Long userId);
}