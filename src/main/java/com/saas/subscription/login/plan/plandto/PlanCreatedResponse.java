package com.saas.subscription.login.plan.plandto;

public class PlanCreatedResponse
{
    private boolean isCreated;
    private String  message;

    public PlanCreatedResponse(boolean isCreated, String message)
    {
        this.isCreated = isCreated;
        this.message = message;
    }
}
