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

    @Column(length = 100)
    private String FirstName;

    @Column(length = 100)
    private String LastName;

    @Column(length = 255, unique = true)
    private String UserEmail;

    @Column(length = 255)
    private String PasswordHash;

//    private String UserStatus;

    @CreatedDate
    private long UserCreateTime;

    private long UserLastUpdatedTime;

//    private int Version;  Thinging why I write this...

}
