package com.saas.subscription.login.logindto;

public class UserSignupResponse<T> {

    private boolean isSuccess;
    private String  message;
    private T data;

    public UserSignupResponse(boolean isSuccess, String message)
    {
        this.isSuccess = isSuccess;
        this.message = message;
//        this.data = data;
    }
}
