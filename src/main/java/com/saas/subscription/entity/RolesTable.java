package com.saas.subscription.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class RolesTable {

    @Id
    private int RoleID;

    @Column(length = 50)
    private String name;
}
