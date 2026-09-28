package com.saas.subscription.plan.plandto;

import com.saas.subscription.entity.PlansTable;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class PlanResponse {

    private final long planId;
    private final String planCode;
    private final String planName;
    private final String planDescription;
    private final BigDecimal planPrice;
    private final PlansTable.BillingIntervalEnum billingInterval;
    private final int trialDays;
    private final PlansTable.PlanStatusEnum planStatus;
    private final LocalDateTime planCreatedTime;
    private final LocalDateTime planLastUpdatedTime;

    public static PlanResponse from(PlansTable plan)
    {
        return PlanResponse.builder()
                .planId(plan.getPlanID())
                .planCode(plan.getPlanCode())
                .planName(plan.getPlanName())
                .planDescription(plan.getPlanDescription())
                .planPrice(plan.getPlanPrice())
                .billingInterval(plan.getBillingInterval())
                .trialDays(plan.getTrialDays())
                .planStatus(plan.getPlanStatus())
                .planCreatedTime(plan.getPlanCreatedTime())
                .planLastUpdatedTime(plan.getPlanLastUpdatedTime())
                .build();
    }
}
