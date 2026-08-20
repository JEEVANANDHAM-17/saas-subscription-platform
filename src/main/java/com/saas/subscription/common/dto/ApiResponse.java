package com.saas.subscription.common.dto;

public record ApiResponse<T>(int code, T message)
{

}
