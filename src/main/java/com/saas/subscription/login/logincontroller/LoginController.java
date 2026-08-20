package com.saas.subscription.login.logincontroller;

import com.saas.subscription.common.dto.ApiResponse;
import com.saas.subscription.login.logindto.UserLoginRequest;
import com.saas.subscription.login.logindto.UserLoginResponse;
import com.saas.subscription.login.logindto.UserSignupRequest;
import com.saas.subscription.login.logindto.UserSignupResponse;
import com.saas.subscription.login.loginservice.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<UserSignupResponse> userSignUP(@Validated @RequestBody UserSignupRequest userSignupRequest) {

        loginService.userSignUP(userSignupRequest);

        return ResponseEntity.ok(new UserSignupResponse(true, "Signup sucessful"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserLoginResponse>> userLogin(
            @Validated @RequestBody UserLoginRequest userLoginRequest)
    {

        UserLoginResponse loginResponse = loginService.userLogin(userLoginRequest);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse<>(201, loginResponse));
    }
}
