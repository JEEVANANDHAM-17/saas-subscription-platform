package com.saas.subscription.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "PlansTable")
public class PlansTable {

    public enum BillingIntervalEnum {
        MONTHLY,
        YEARLY
    }

    public enum PlanStatusEnum {
        DRAFT,
        ACTIVE,
        ARCHIVED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PlanID")
    private long planID;

    @Column(name = "PlanCode", length = 50, nullable = false, unique = true)
    private String planCode;

    @Column(name = "PlanName", length = 100, nullable = false)
    private String planName;

    @Column(name = "PlanDescription", length = 500)
    private String planDescription;

    @Column(name = "PlanPrice", precision = 19, scale = 4, nullable = false)
    private BigDecimal planPrice;

//    @Column(name = "Currency", length = 3, nullable = false)
//    private String currency; for now currency is not needed.

    @Enumerated(EnumType.STRING)
    @Column(name = "BillingInterval", nullable = false)
    private BillingIntervalEnum billingInterval;

    @Column(name = "TrialDays", nullable = false)
    private int trialDays;

    @Enumerated(EnumType.STRING)
    @Column(name = "PlanStatus", nullable = false)
    private PlanStatusEnum planStatus = PlanStatusEnum.DRAFT;

    @Column(name = "PlanCreatedTime", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime planCreatedTime;

    @Column(name = "PlanLastUpdatedTime", nullable = false)
    @UpdateTimestamp
    private LocalDateTime planLastUpdatedTime;
}
