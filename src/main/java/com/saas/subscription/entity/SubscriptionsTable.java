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
    private long subscriptionID;

    @ManyToOne(optional = false)
    @JoinColumn(name = "CustomerID", nullable = false)
    private CustomerTable customerID;

    @Column(name = "PlanID", nullable = false)
    private long planID;

    @Enumerated(EnumType.STRING)
    @Column(name = "SubscriptionStatus", nullable = false)
    private SubscriptionStatusEnum subscriptionStatus = SubscriptionStatusEnum.ACTIVE;

    @CreatedDate
    @Column(name = "SubscriptionCreatedDate", nullable = false)
    private long subscriptionCreatedDate;

    @Column(name = "SubscriptionUpdateDate", nullable = false)
    private long subscriptionUpdateDate;

    @Column(name = "SubscriptionTrialEndsDate")
    private long subscriptionTrialEndsDate;

    @Column(name = "SubscriptionCurrentCycleStartDate", nullable = false)
    private long subscriptionCurrentCycleStartDate;

    @Column(name = "SubscriptionCurrentCycleEndDate", nullable = false)
    private long subscriptionCurrentCycleEndDate;

    @Column(name = "CancelAtEnd", nullable = false)
    private boolean cancelAtEnd;

}
