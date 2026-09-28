package com.saas.subscription.plan.plandto;

import com.saas.subscription.entity.PlansTable;
import lombok.Data;

@Data
public class PlanUpdateRequest
{
//    @NotNull
//    private PlansTable.BillingIntervalEnum billingInterval; // In future will give support
    //because there is a case when it is update able only when no subscription is useing this plan.. so needs to handle
    //for simplecity just be simple when starting

    private String planDescription;

    private String planName;

    private Double planPrice;

    private PlansTable.PlanStatusEnum planStatus;
}

