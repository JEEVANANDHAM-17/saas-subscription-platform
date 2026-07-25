package com.saas.subscription.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class SubscriptionHistoryTable {

    @Id
    private long SubscriptionHistoryID;

    private long SubscriptionID;

    private long OldPlanId;

    private long NewPlanId;

    private int ChangeType;

    private long ChangedDate;

    private double ProrationAmount;

    private long SubscriptionChangedTime;
}
