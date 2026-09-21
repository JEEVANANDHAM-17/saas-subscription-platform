package com.saas.subscription.security;

import com.saas.subscription.core.controller.SubscriptionController;
import com.saas.subscription.entity.UsersTable;
import com.saas.subscription.login.logincontroller.LoginController;
import com.saas.subscription.login.logindto.UserLoginResponse;
import com.saas.subscription.login.loginservice.LoginService;
import com.saas.subscription.plan.plancontroller.PlanController;
import com.saas.subscription.plan.planservice.PlanService;
import com.saas.subscription.security.jwt.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {LoginController.class, PlanController.class, SubscriptionController.class})
@Import(SecurityConfig.class)
class SecurityConfigTests {

    private static final String LOGIN_BODY = "{\"user_email\":\"user@example.com\",\"user_password\":\"secret\"}";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LoginService loginService;

    @MockitoBean
    private PlanService planService;

    @MockitoBean
    private JwtService jwtService;

    // @EnableJpaAuditing on SubscriptionApplication needs a JPA metamodel, which this web slice does not start.
    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    void loginIsReachableWithoutAToken() throws Exception
    {
        when(loginService.userLogin(any()))
                .thenReturn(new UserLoginResponse("Login successful", "signed.jwt.token", "Bearer", 3600L));

        mockMvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON).content(LOGIN_BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message.access_token").value("signed.jwt.token"));
    }

    @Test
    void loginWithWrongCredentialsIsAnsweredWith401() throws Exception
    {
        when(loginService.userLogin(any())).thenThrow(new BadCredentialsException("Invalid email or password"));

        mockMvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON).content(LOGIN_BODY))
                .andExpect(status().isUnauthorized())
                .andExpect(content().json("{\"code\":401,\"message\":\"Invalid email or password\"}"));
    }

    @Test
    void everyOtherEndpointRequiresAToken() throws Exception
    {
        mockMvc.perform(get("/")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/testapi")).andExpect(status().isUnauthorized());
        mockMvc.perform(post("/signup").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(post("/plan").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string(HttpHeaders.WWW_AUTHENTICATE, "Bearer"))
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").isNotEmpty());

        verifyNoInteractions(loginService, planService);
    }

    @Test
    void anInvalidTokenIsRejected() throws Exception
    {
        when(jwtService.validateJWTToken("expired.jwt.token"))
                .thenThrow(new BadCredentialsException("Invalid or expired token"));

        mockMvc.perform(get("/testapi").header(HttpHeaders.AUTHORIZATION, "Bearer expired.jwt.token"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().json("{\"code\":401,\"message\":\"Invalid or expired token\"}"));
    }

    @Test
    void aValidTokenGrantsAccess() throws Exception
    {
        when(jwtService.validateJWTToken("valid.jwt.token"))
                .thenReturn(UsersTable.builder().userID(42L).userEmail("user@example.com").build());

        mockMvc.perform(get("/testapi").header(HttpHeaders.AUTHORIZATION, "Bearer valid.jwt.token"))
                .andExpect(status().isOk())
                .andExpect(content().string("Test Api Response"));
    }
}
