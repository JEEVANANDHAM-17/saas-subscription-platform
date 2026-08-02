package com.saas.subscription.login.logindto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserLoginRequest
{
    @NotNull
    private String userEmail;

    @NotNull
    private String userPassword;
}
