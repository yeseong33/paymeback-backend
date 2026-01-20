package com.paymeback.domain.user.controller;

import com.paymeback.common.response.ApiResponse;
import com.paymeback.domain.user.dto.SignInRequest;
import com.paymeback.domain.user.dto.SignUpRequest;
import com.paymeback.domain.user.dto.UserResponse;
import com.paymeback.domain.user.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserResponse>> signUp(@Valid @RequestBody SignUpRequest request) {
        UserResponse userResponse = authService.signUp(request);
        return ResponseEntity.ok(ApiResponse.success(userResponse, "회원가입이 완료되었습니다. 이메일을 확인해주세요."));
    }

    @PostMapping("/signin")
    public ResponseEntity<ApiResponse<Map<String, String>>> signIn(@Valid @RequestBody SignInRequest request) {
        String token = authService.signIn(request);
        Map<String, String> tokenMap = Map.of("accessToken", token);
        return ResponseEntity.ok(ApiResponse.success(tokenMap, "로그인이 완료되었습니다."));
    }
}
