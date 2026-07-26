package com.saas.subscription.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class SubscriptionHistoryTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long SubscriptionHistoryID;

    @ManyToOne(optional = false)
    @JoinColumn(name = "subscription_id", nullable = false)
    private SubscriptionsTable SubscriptionID;

    private long OldPlanId;

    private long NewPlanId;

    @Column(nullable = false)
    private int ChangeType;

    @Column(nullable = false)
    private long ChangedDate;

    private double ProrationAmount;

    private long SubscriptionChangedTime;
}
