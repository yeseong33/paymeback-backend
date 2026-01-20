package com.paymeback.domain.user.controller;

import com.paymeback.common.response.ApiResponse;
import com.paymeback.domain.user.dto.SignInRequest;
import com.paymeback.domain.user.dto.SignUpRequest;
import com.paymeback.domain.user.dto.UserResponse;
import com.paymeback.domain.user.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Auth", description = "인증 API")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회원가입 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "이미 존재하는 이메일")
    })
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserResponse>> signUp(@Valid @RequestBody SignUpRequest request) {
        UserResponse userResponse = authService.signUp(request);
        return ResponseEntity.ok(ApiResponse.success(userResponse, "회원가입이 완료되었습니다. 이메일을 확인해주세요."));
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인합니다.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그인 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PostMapping("/signin")
    public ResponseEntity<ApiResponse<Map<String, String>>> signIn(@Valid @RequestBody SignInRequest request) {
        String token = authService.signIn(request);
        Map<String, String> tokenMap = Map.of("accessToken", token);
        return ResponseEntity.ok(ApiResponse.success(tokenMap, "로그인이 완료되었습니다."));
    }
}
