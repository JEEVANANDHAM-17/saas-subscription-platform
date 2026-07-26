package com.saas.subscription.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;

@Entity
public class UsersTable {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private long UserID;

//    @Column(unique = true)
//    private String uuid;

    @Column(length = 100, nullable = false)
    private String FirstName;

    @Column(length = 100)
    private String LastName;

    @Column(length = 255, nullable = false, unique = true)
    private String UserEmail;

    @Column(length = 255, nullable = false)
    private String PasswordHash;

//    private String UserStatus;

    @CreatedDate
    @Column(nullable = false)
    private long UserCreateTime;

    @Column(nullable = false)
    private long UserLastUpdatedTime;

//    private int Version;  Thinging why I write this...

}
