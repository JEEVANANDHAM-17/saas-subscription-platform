package com.saas.subscription.login.logincontroller;

import com.saas.subscription.login.logindto.UserSignupRequest;
import com.saas.subscription.login.logindto.UserSignupResponse;
import com.saas.subscription.login.loginservice.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/signup")
    public ResponseEntity<UserSignupResponse> userSignUP(@Validated @RequestBody UserSignupRequest userLoginRequest) {

        loginService.userSignUP(userLoginRequest);

        return ResponseEntity.ok(new UserSignupResponse(true, "Signup sucessful"));
    }
}
