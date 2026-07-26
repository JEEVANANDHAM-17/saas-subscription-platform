package com.saas.subscription.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;

@Entity
@Table(name = "UsersTable")
public class UsersTable {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "UserID")
    private long UserID;

//    @Column(unique = true)
//    private String uuid;

    @Column(name = "FirstName", length = 100, nullable = false)
    private String FirstName;

    @Column(name = "LastName", length = 100)
    private String LastName;

    @Column(name = "UserEmail", length = 255, nullable = false, unique = true)
    private String UserEmail;

    @Column(name = "PasswordHash", length = 255, nullable = false)
    private String PasswordHash;

//    private String UserStatus;

    @CreatedDate
    @Column(name = "UserCreateTime", nullable = false)
    private long UserCreateTime;

    @Column(name = "UserLastUpdatedTime", nullable = false)
    private long UserLastUpdatedTime;

//    private int Version;  Thinging why I write this...

}
