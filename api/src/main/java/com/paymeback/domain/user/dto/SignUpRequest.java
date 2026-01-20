package com.paymeback.domain.user.dto;



public record SignUpRequest(

    String email,

    String password,

    String confirmPassword,

    String name
) {
}