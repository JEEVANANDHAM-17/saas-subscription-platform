package com.saas.subscription.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

@Entity
@Table(name = "UsersTable")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsersTable {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "UserID")
    private long UserID;

//    @Column(unique = true)
//    private String uuid;

    @Column(name = "FirstName", length = 100, nullable = false)
    private String firstName;

    @Column(name = "LastName", length = 100)
    private String lastName;

    @Column(name = "UserEmail", length = 255, nullable = false, unique = true)
    private String userEmail;

    @Column(name = "PasswordHash", length = 255, nullable = false)
    private String passwordHash;

//    private String UserStatus;

    @CreatedDate
    @Column(name = "UserCreateTime", nullable = false)
    private long userCreateTime;

    @Column(name = "UserLastUpdatedTime", nullable = false)
    private long userLastUpdatedTime;

//    private int Version;  Thinging why I write this...

}
