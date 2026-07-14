package com.saas.subscription.subscription.subscriptioncore.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SubscriptionController {

    @GetMapping("/")
    public String home()
    {
        return "Subscription application is running";
    }

    @GetMapping("/testapi")
    public String testApi()
    {
        return "Test Api Response";
    }
}
