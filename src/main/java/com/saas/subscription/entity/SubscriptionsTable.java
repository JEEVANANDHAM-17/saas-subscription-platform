package com.saas.subscription.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;

@Entity
@Table(name = "SubscriptionsTable")
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
    @Column(name = "SubscriptionID")
    private long SubscriptionID;

    @ManyToOne(optional = false)
    @JoinColumn(name = "CustomerID", nullable = false)
    private CustomerTable CustomerID;

    @Column(name = "PlanID", nullable = false)
    private long PlanID;

    @Enumerated(EnumType.STRING)
    @Column(name = "SubscriptionStatus", nullable = false)
    private SubscriptionStatusEnum SubscriptionStatus = SubscriptionStatusEnum.ACTIVE;

    @CreatedDate
    @Column(name = "SubscriptionCreatedDate", nullable = false)
    private long SubscriptionCreatedDate;

    @Column(name = "SubscriptionUpdateDate", nullable = false)
    private long SubscriptionUpdateDate;

    @Column(name = "SubscriptionTrialEndsDate")
    private long SubscriptionTrialEndsDate;

    @Column(name = "SubscriptionCurrentCycleStartDate", nullable = false)
    private long SubscriptionCurrentCycleStartDate;

    @Column(name = "SubscriptionCurrentCycleEndDate", nullable = false)
    private long SubscriptionCurrentCycleEndDate;

    @Column(name = "CancelAtEnd", nullable = false)
    private boolean CancelAtEnd;

}
