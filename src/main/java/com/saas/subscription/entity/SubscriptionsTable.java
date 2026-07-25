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
    private long SubscriptionID;

    private long CustomerID;

    private long PlanID;

    @Enumerated(EnumType.STRING)
    private SubscriptionStatusEnum SubscriptionStatus = SubscriptionStatusEnum.ACTIVE;

    @CreatedDate
    @Column(nullable = false)
    private long SubscriptionCreatedDate;

    private long SubscriptionUpdateDate;

    private long SubscriptionTrialEndsDate;

    private long SubscriptionCurrentCycleStartDate;

    private long SubscriptionCurrentCycleEndDate;

    private boolean CancelAtEnd;

}
