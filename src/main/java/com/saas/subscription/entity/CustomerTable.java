package com.saas.subscription.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class CustomerTable {

    public enum CustomerStatusEnum {
        ACTIVE,
        INACTIVE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long CustomerID;

    @Column(length = 200, nullable = false)
    private String CustomerName;

    @Column(length = 255, nullable = false, unique = true)
    private String CustomerEmail;

    @Column(length = 30)
    private long CustomerPhone;

    @Column(length = 255)
    private String CustomerBillingAddress;
    // For First release it will be json and future it will referenced to another table

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomerStatusEnum CustomerStatus = CustomerStatusEnum.ACTIVE;

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private long CustomerCreatedTime;

    @Column(nullable = false)
    @LastModifiedDate
    private long CustomerLastUpdateTime;
}
