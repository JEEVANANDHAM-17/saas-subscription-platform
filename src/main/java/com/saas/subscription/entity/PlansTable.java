package com.saas.subscription.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
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
    private long PlanID;

    @Column(name = "PublicID", length = 36, nullable = false, unique = true)
    private String PublicID;

    @Column(name = "PlanCode", length = 50, nullable = false, unique = true)
    private String PlanCode;

    @Column(name = "PlanName", length = 100, nullable = false)
    private String PlanName;

    @Column(name = "PlanDescription", length = 500)
    private String PlanDescription;

    @Column(name = "PlanPrice", precision = 19, scale = 4, nullable = false)
    private BigDecimal PlanPrice;

    @Column(name = "Currency", length = 3, nullable = false)
    private String Currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "BillingInterval", nullable = false)
    private BillingIntervalEnum BillingInterval;

    @Column(name = "TrialDays", nullable = false)
    private int TrialDays;

    @Enumerated(EnumType.STRING)
    @Column(name = "PlanStatus", nullable = false)
    private PlanStatusEnum PlanStatus = PlanStatusEnum.DRAFT;

    @Column(name = "PlanCreatedTime", nullable = false, updatable = false)
    private LocalDateTime PlanCreatedTime;

    @Column(name = "PlanLastUpdatedTime", nullable = false)
    private LocalDateTime PlanLastUpdatedTime;
}
