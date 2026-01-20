package com.paymeback.domain.payment.controller;

import com.paymeback.common.response.ApiResponse;
import com.paymeback.domain.payment.dto.PaymentResponse;
import com.paymeback.domain.payment.dto.ProcessPaymentRequest;
import com.paymeback.domain.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/gatherings/{gatheringId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> processPayment(
        @PathVariable Long gatheringId,
        @Valid @RequestBody ProcessPaymentRequest request,
        @AuthenticationPrincipal UserDetails userDetails) {

        PaymentResponse response = paymentService.processPayment(gatheringId, request, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response, "결제가 완료되었습니다."));
    }

    @GetMapping("/gatherings/{gatheringId}/my")
    public ResponseEntity<ApiResponse<PaymentResponse>> getMyPaymentStatus(
        @PathVariable Long gatheringId,
        @AuthenticationPrincipal UserDetails userDetails) {

        PaymentResponse response = paymentService.getPaymentStatus(gatheringId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/gatherings/{gatheringId}")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getGatheringPayments(
        @PathVariable Long gatheringId,
        @AuthenticationPrincipal UserDetails userDetails) {

        List<PaymentResponse> response = paymentService.getGatheringPayments(gatheringId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}