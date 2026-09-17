package com.saas.subscription.login.plan.planrepository;

import com.saas.subscription.entity.PlansTable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanRepository extends JpaRepository<PlansTable, Long>
{
}
