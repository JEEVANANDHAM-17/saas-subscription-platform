package com.saas.subscription.plan.planservice;

import com.saas.subscription.entity.PlansTable;
import com.saas.subscription.plan.plandto.PlanCreationRequest;
import com.saas.subscription.plan.plandto.PlanResponse;
import com.saas.subscription.plan.plandto.PlanUpdateRequest;
import com.saas.subscription.plan.planrepository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
public class PlanService {

    private final PlanRepository planRepository;

    public PlansTable createPlan(PlanCreationRequest planCreationRequest)
    {
        PlansTable plansTable = PlansTable.builder()
                .billingInterval(planCreationRequest.getBillingInterval())
                .planCode(planCreationRequest.getPlanCode())
                .planDescription(planCreationRequest.getPlanDescription())
                .planName(planCreationRequest.getPlanName())
                .planPrice(BigDecimal.valueOf(planCreationRequest.getPlanPrice()))
                .planStatus(planCreationRequest.getPlanStatus())
                .build();

        return planRepository.save(plansTable);
    }

    public PlansTable updatePlan(long id, PlanUpdateRequest planUpdateRequest)
    {
        PlansTable plansTable = getPlan(id);

        if (planUpdateRequest.getPlanDescription() != null)
        {
            plansTable.setPlanDescription(planUpdateRequest.getPlanDescription());
        }
        if (planUpdateRequest.getPlanName() != null)
        {
            plansTable.setPlanName(planUpdateRequest.getPlanName());
        }
        if (planUpdateRequest.getPlanPrice() != null)
        {
            plansTable.setPlanPrice(BigDecimal.valueOf(planUpdateRequest.getPlanPrice()));
        }
        if (planUpdateRequest.getPlanStatus() != null)
        {
            plansTable.setPlanStatus(planUpdateRequest.getPlanStatus());
        }

        return planRepository.save(plansTable);
    }

    public PlansTable getPlan(long id)
    {
        return planRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan not found"));
    }

}
