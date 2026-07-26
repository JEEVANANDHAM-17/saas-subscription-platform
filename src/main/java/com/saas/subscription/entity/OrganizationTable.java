package com.saas.subscription.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.springframework.data.annotation.CreatedDate;

@Entity
@Table(name = "OrganizationTable")
public class OrganizationTable {

    @Id
    @Column(name = "OrganizationID")
    private long OrganizationID;

    @Column(name = "name", length = 200)
    private String name;

//    private EnumOfOrgStatus status; will Implement later

    @Column(name = "OrganizationCreatedTime", nullable = false, updatable = false)
    @CreatedDate
    private long OrganizationCreatedTime;
}
