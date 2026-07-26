package com.saas.subscription.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;

@Entity
public class SubscriptionsTable {

    public enum SubscriptionStatusEnum {
        ACTIVE,
        INACTIVE,
        PAST_DUE,
        CANCELLED,
        EXPIRED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long SubscriptionID;

    @ManyToOne(optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerTable CustomerID;

    @Column(nullable = false)
    private long PlanID;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionStatusEnum SubscriptionStatus = SubscriptionStatusEnum.ACTIVE;

    @CreatedDate
    @Column(nullable = false)
    private long SubscriptionCreatedDate;

    @Column(nullable = false)
    private long SubscriptionUpdateDate;

    private long SubscriptionTrialEndsDate;

    @Column(nullable = false)
    private long SubscriptionCurrentCycleStartDate;

    @Column(nullable = false)
    private long SubscriptionCurrentCycleEndDate;

    @Column(nullable = false)
    private boolean CancelAtEnd;

}
