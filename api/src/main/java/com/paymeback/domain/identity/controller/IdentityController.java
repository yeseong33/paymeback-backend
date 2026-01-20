package com.paymeback.domain.identity.controller;

import com.paymeback.common.response.ApiResponse;
import com.paymeback.domain.identity.service.IdentityService;
import com.paymeback.domain.user.dto.OtpVerificationRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Identity", description = "본인 인증 API")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class IdentityController {

    private final IdentityService identityService;

    @Operation(summary = "OTP 인증", description = "이메일로 받은 OTP 코드를 검증합니다.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "인증 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 OTP 코드"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<Void>> verifyOtp(@Valid @RequestBody OtpVerificationRequest request) {
        identityService.verifyOtp(request.email(), request.otpCode());
        return ResponseEntity.ok(ApiResponse.success("이메일 인증이 완료되었습니다."));
    }

    @Operation(summary = "OTP 재발송", description = "인증 코드를 이메일로 재발송합니다.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "재발송 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "429", description = "요청 횟수 초과")
    })
    @PostMapping("/resend-otp")
    public ResponseEntity<ApiResponse<Void>> resendOtp(
        @Parameter(description = "이메일 주소") @RequestParam String email) {
        identityService.resendOtp(email);
        return ResponseEntity.ok(ApiResponse.success("인증 코드가 재발송되었습니다."));
    }
}