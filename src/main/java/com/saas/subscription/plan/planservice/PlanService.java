package com.saas.subscription.plan.planservice;

import com.saas.subscription.entity.PlansTable;
import com.saas.subscription.plan.plandto.PlanCreationRequest;
import com.saas.subscription.plan.planrepository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
public class PlanService {

    private final PlanRepository planRepository;

    public void createPlan(PlanCreationRequest planCreationRequest)
    {
        PlansTable plansTable = PlansTable.builder()
                .billingInterval(planCreationRequest.getBillingInterval())
                .planCode(planCreationRequest.getPlanCode())
                .planDescription(planCreationRequest.getPlanDescription())
                .planName(planCreationRequest.getPlanName())
                .planPrice(BigDecimal.valueOf(planCreationRequest.getPlanPrice()))
                .planStatus(planCreationRequest.getPlanStatus())
                .trialDays(planCreationRequest.getTrialDays())
                .build();

        planRepository.save(plansTable);

    }
}
