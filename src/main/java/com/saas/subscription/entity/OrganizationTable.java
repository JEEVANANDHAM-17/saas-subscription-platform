package com.saas.subscription.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.springframework.data.annotation.CreatedDate;

@Entity
public class OrganizationTable {

    @Id
    private long OrganizationID;

    @Column(length = 200)
    private String name;

//    private EnumOfOrgStatus status; will Implement later

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private long OrganizationCreatedTime;
}
