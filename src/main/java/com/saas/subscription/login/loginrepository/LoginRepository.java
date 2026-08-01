package com.saas.subscription.login.loginrepository;

import com.saas.subscription.entity.UsersTable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginRepository extends JpaRepository<UsersTable, Long>
{

}
