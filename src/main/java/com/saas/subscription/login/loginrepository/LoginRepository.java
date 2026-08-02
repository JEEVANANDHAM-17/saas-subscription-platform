package com.saas.subscription.login.loginrepository;

import com.saas.subscription.entity.UsersTable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoginRepository extends JpaRepository<UsersTable, Long>
{
    Optional<UsersTable> findByUserEmail(String userEmail);
}
