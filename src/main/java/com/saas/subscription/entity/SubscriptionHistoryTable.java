package com.saas.subscription.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "SubscriptionHistoryTable")
public class SubscriptionHistoryTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SubscriptionHistoryID")
    private long subscriptionHistoryID;

    @ManyToOne(optional = false)
    @JoinColumn(name = "SubscriptionID", nullable = false)
    private SubscriptionsTable subscriptionID;

    @Column(name = "OldPlanId")
    private long oldPlanId;

    @Column(name = "NewPlanId")
    private long newPlanId;

    @Column(name = "ChangeType", nullable = false)
    private int changeType;

    @Column(name = "ChangedDate", nullable = false)
    private long changedDate;

    @Column(name = "ProrationAmount")
    private double prorationAmount;

    @Column(name = "SubscriptionChangedTime")
    private long subscriptionChangedTime;
}
