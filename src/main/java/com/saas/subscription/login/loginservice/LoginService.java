package com.saas.subscription.login.loginservice;

import com.saas.subscription.entity.UsersTable;
import com.saas.subscription.login.logindto.UserSignupRequest;
import com.saas.subscription.login.loginrepository.LoginRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.security.crypto.password.PasswordEncoder;


@RequiredArgsConstructor
@Service
public class LoginService {

    private final LoginRepository loginRepository;

    private final PasswordEncoder passwordEncoder;


    public void userSignUP(UserSignupRequest userLoginRequest)
    {
        UsersTable usersTable = UsersTable.builder()
                .UserEmail(userLoginRequest.getUserEmail())
                .PasswordHash(passwordEncoder.encode(userLoginRequest.getUserPassword()))
                .FirstName(userLoginRequest.getFirstName())
                .build();

        loginRepository.save(usersTable);
    }
}
