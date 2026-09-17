package com.saas.subscription.plan.plancontroller;

import com.saas.subscription.plan.plandto.PlanCreatedResponse;
import com.saas.subscription.plan.plandto.PlanCreationRequest;
import com.saas.subscription.plan.planservice.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;
    private PlanService service;

    @PostMapping("/plan")
    public ResponseEntity<PlanCreatedResponse> planCreation(@Validated @RequestBody PlanCreationRequest planCreationRequest)
    {
        planService.createPlan(planCreationRequest);// In future send plan id in the response

        return ResponseEntity.ok(new PlanCreatedResponse(true, "Signup sucessful"));
    }
}
