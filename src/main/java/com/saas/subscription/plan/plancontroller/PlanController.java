package com.saas.subscription.plan.plancontroller;

import com.saas.subscription.plan.plandto.PlanCreationRequest;
import com.saas.subscription.plan.planservice.PlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RequiredArgsConstructor
@Controller
public class PlanController
{
    private final PlanService planService;

    @PostMapping("/plan")
    public ResponseEntity<String> createPlan(
            @Valid @RequestBody PlanCreationRequest planCreationRequest
    )
    {
        planService.createPlan(planCreationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("Plan created");
    }
}
