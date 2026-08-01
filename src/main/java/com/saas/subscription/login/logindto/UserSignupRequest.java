package com.saas.subscription.login.logindto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserSignupRequest {

    @NotBlank
    private String userEmail;

    @NotBlank
    private String userPassword;

    private String firstName;

    private String lastName;
}
