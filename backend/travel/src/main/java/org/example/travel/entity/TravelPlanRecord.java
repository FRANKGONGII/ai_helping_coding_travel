package org.example.travel.entity;


import jakarta.persistence.*;
import lombok.Data;



import java.time.LocalDateTime;

@Entity
@Table(name = "travel_plan_records")
@Data
public class TravelPlanRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "travel_plan_id", nullable = false)
    private Long travelPlanId;

    @Column
    private String content;

    @Column
    private Double money;

    @Column
    private LocalDateTime consumptionTime;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}