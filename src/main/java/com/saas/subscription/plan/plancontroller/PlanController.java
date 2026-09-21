package com.saas.subscription.plan.plancontroller;

import com.saas.subscription.entity.PlansTable;
import com.saas.subscription.plan.plandto.PlanCreationRequest;
import com.saas.subscription.plan.plandto.PlanResponse;
import com.saas.subscription.plan.plandto.PlanUpdateRequest;
import com.saas.subscription.plan.planservice.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;
    private PlanService service;

    @PostMapping("/plan")
    public ResponseEntity<PlanResponse> planCreation(@Validated @RequestBody PlanCreationRequest planCreationRequest)
    {
        PlansTable plan = planService.createPlan(planCreationRequest);// In future send plan id in the response

        return ResponseEntity.ok(PlanResponse.from(plan));
    }

    @PutMapping("/plan/{id}")
    public ResponseEntity<PlanResponse> updatePlan(@PathVariable("id") long id, @Validated @RequestBody
                                                       PlanUpdateRequest planUpdateRequest)
    {
        PlansTable plan = planService.updatePlan(id, planUpdateRequest);

        return ResponseEntity.ok(PlanResponse.from(plan));
    }

    @GetMapping("/plan/{id}")
    public ResponseEntity<PlanResponse> getPlan(@PathVariable("id") long id)
    {
        PlansTable plan = planService.getPlan(id);

        return ResponseEntity.ok(PlanResponse.from(plan));
    }
}
