package com.paymeback.domain.user.dto;


public record SignInRequest(

    String email,

    String password
) {
}