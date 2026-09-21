package com.saas.subscription.plan.plandto;

import com.saas.subscription.entity.PlansTable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class PlanCreationRequest
{
    @NotNull
    private PlansTable.BillingIntervalEnum billingInterval;

    @NotNull
    private String planCode;

    private String planDescription;

    @NotNull
    private String planName;

    @NotNull
    private double planPrice;

    @PositiveOrZero
    private int trialDays;

    @NotNull
    private PlansTable.PlanStatusEnum planStatus;
}

