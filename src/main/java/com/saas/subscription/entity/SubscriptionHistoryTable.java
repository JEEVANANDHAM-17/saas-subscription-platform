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
    private long SubscriptionHistoryID;

    @ManyToOne(optional = false)
    @JoinColumn(name = "SubscriptionID", nullable = false)
    private SubscriptionsTable SubscriptionID;

    @Column(name = "OldPlanId")
    private long OldPlanId;

    @Column(name = "NewPlanId")
    private long NewPlanId;

    @Column(name = "ChangeType", nullable = false)
    private int ChangeType;

    @Column(name = "ChangedDate", nullable = false)
    private long ChangedDate;

    @Column(name = "ProrationAmount")
    private double ProrationAmount;

    @Column(name = "SubscriptionChangedTime")
    private long SubscriptionChangedTime;
}
