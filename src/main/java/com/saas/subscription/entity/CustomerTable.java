package com.saas.subscription.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "CustomerTable")
@EntityListeners(AuditingEntityListener.class)
public class CustomerTable {

    public enum CustomerStatusEnum {
        ACTIVE,
        INACTIVE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CustomerID")
    private long CustomerID;

    @Column(name = "CustomerName", length = 200, nullable = false)
    private String CustomerName;

    @Column(name = "CustomerEmail", length = 255, nullable = false, unique = true)
    private String CustomerEmail;

    @Column(name = "CustomerPhone", length = 30)
    private long CustomerPhone;

    @Column(name = "CustomerBillingAddress", length = 255)
    private String CustomerBillingAddress;
    // For First release it will be json and future it will referenced to another table

    @Enumerated(EnumType.STRING)
    @Column(name = "CustomerStatus", nullable = false)
    private CustomerStatusEnum CustomerStatus = CustomerStatusEnum.ACTIVE;

    @Column(name = "CustomerCreatedTime", nullable = false, updatable = false)
    @CreatedDate
    private long CustomerCreatedTime;

    @Column(name = "CustomerLastUpdateTime", nullable = false)
    @LastModifiedDate
    private long CustomerLastUpdateTime;
}
