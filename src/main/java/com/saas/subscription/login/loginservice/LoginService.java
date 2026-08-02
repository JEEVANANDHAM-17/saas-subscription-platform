package com.saas.subscription.login.loginservice;

import com.saas.subscription.entity.UsersTable;
import com.saas.subscription.login.logindto.UserLoginRequest;
import com.saas.subscription.login.logindto.UserLoginResponse;
import com.saas.subscription.login.logindto.UserSignupRequest;
import com.saas.subscription.login.loginrepository.LoginRepository;
import com.saas.subscription.security.jwt.JwtService;
import com.saas.subscription.security.jwt.JwtToken;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class LoginService {

    private final LoginRepository loginRepository;
    private final JwtService jwtService;


    public void userSignUP(UserSignupRequest userLoginRequest)
    {
        UsersTable usersTable = UsersTable.builder()
                .userEmail(userLoginRequest.getUserEmail())
                .passwordHash(jwtService.encodePassword(userLoginRequest.getUserPassword()))
                .firstName(userLoginRequest.getFirstName())
                .build();

        loginRepository.save(usersTable);
    }

    public UserLoginResponse userLogin(UserLoginRequest userLoginRequest) {
        UsersTable usersTable = loginRepository.findByUserEmail(userLoginRequest.getUserEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!jwtService.matchesPassword(userLoginRequest.getUserPassword(), usersTable.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        JwtToken token = jwtService.generateJWTToken(usersTable);
        return new UserLoginResponse(
                "Login successful",
                token.accessToken(),
                token.tokenType(),
                token.expiresIn()
        );
    }
}
