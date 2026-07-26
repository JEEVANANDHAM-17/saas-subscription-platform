package com.saas.subscription.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "RolesTable")
public class RolesTable {

    @Id
    @Column(name = "RoleID")
    private int RoleID;

    @Column(name = "name", length = 50)
    private String name;
}
