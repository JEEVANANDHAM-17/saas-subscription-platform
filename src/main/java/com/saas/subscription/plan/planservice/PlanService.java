package com.saas.subscription.plan.planservice;

import com.saas.subscription.entity.PlansTable;
import com.saas.subscription.plan.plandto.PlanCreationRequest;
import com.saas.subscription.plan.planrepository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PlanService
{

    private final PlanRepository planRepository;

    public void createPlan(PlanCreationRequest planCreationRequest)
    {
        PlansTable plansTable = PlansTable.builder()
                .planCode(planCreationRequest.getPlanCode())
                .planName(planCreationRequest.getPlanName())
                .planDescription(planCreationRequest.getPlanDescription())
                .planPrice(planCreationRequest.getPlanPrice())
                .billingInterval(planCreationRequest.getBillingInterval())
                .trialDays(planCreationRequest.getTrialDays())
                .planStatus(planCreationRequest.getPlanStatus())
                .build();

        planRepository.save(plansTable);
    }
}
