package com.saas.subscription.plan.plandto;

import com.saas.subscription.entity.PlansTable;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PlanCreationRequest
{
    @NotNull
    private String planCode;

    @NotNull
    private String planName;

    private String planDescription;

    @NotNull(message = "Plan price is required")
    @DecimalMin(value = "0.0000", inclusive = true,
            message = "Plan price cannot be negative")
    @Digits(integer = 15, fraction = 4,
            message = "Plan price must have at most 15 integer digits and 4 decimal places")
    private BigDecimal planPrice;

//    private String currency;

    @NotNull(message = "Billing interval is required")
    private PlansTable.BillingIntervalEnum billingInterval;

    @PositiveOrZero(message = "Trial days cannot be negative")
    private int trialDays = 0;

    private PlansTable.PlanStatusEnum planStatus = PlansTable.PlanStatusEnum.ACTIVE;
}
