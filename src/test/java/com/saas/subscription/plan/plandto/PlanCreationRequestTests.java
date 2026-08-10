package com.saas.subscription.plan.plandto;

import com.saas.subscription.entity.PlansTable;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PlanCreationRequestTests {

    @Autowired
    private JsonMapper jsonMapper;

    @Test
    void deserializesSnakeCaseApiFields() {
        String json = """
                {
                  "plan_code": "PRO",
                  "plan_name": "Professional",
                  "plan_description": "Professional plan",
                  "plan_price": 49.9900,
                  "billing_interval": "MONTHLY",
                  "trial_days": 14,
                  "plan_status": "ACTIVE"
                }
                """;

        PlanCreationRequest request = jsonMapper.readValue(json, PlanCreationRequest.class);

        assertThat(request.getPlanCode()).isEqualTo("PRO");
        assertThat(request.getPlanName()).isEqualTo("Professional");
        assertThat(request.getPlanDescription()).isEqualTo("Professional plan");
        assertThat(request.getPlanPrice()).isEqualByComparingTo(new BigDecimal("49.9900"));
        assertThat(request.getBillingInterval()).isEqualTo(PlansTable.BillingIntervalEnum.MONTHLY);
        assertThat(request.getTrialDays()).isEqualTo(14);
        assertThat(request.getPlanStatus()).isEqualTo(PlansTable.PlanStatusEnum.ACTIVE);
    }
}
