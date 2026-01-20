package com.paymeback.domain.payment.controller;

import com.paymeback.common.response.ApiResponse;
import com.paymeback.domain.payment.dto.SettlementResponse;
import com.paymeback.domain.payment.service.SettlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/settlements")
@RequiredArgsConstructor
public class SettlementController {

    private final SettlementService settlementService;

    @PostMapping("/calculate/{gatheringId}")
    public ResponseEntity<ApiResponse<List<SettlementResponse>>> calculateSettlements(
        @PathVariable Long gatheringId,
        @AuthenticationPrincipal UserDetails userDetails) {

        List<SettlementResponse> response = settlementService.calculateSettlements(gatheringId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response, "정산이 계산되었습니다."));
    }

    @GetMapping("/gathering/{gatheringId}")
    public ResponseEntity<ApiResponse<List<SettlementResponse>>> getSettlementsByGathering(
        @PathVariable Long gatheringId,
        @AuthenticationPrincipal UserDetails userDetails) {

        List<SettlementResponse> response = settlementService.getSettlementsByGathering(gatheringId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/my/to-send")
    public ResponseEntity<ApiResponse<List<SettlementResponse>>> getMySettlementsToSend(
        @AuthenticationPrincipal UserDetails userDetails) {

        List<SettlementResponse> response = settlementService.getMySettlementsToSend(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/my/to-receive")
    public ResponseEntity<ApiResponse<List<SettlementResponse>>> getMySettlementsToReceive(
        @AuthenticationPrincipal UserDetails userDetails) {

        List<SettlementResponse> response = settlementService.getMySettlementsToReceive(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{settlementId}/complete")
    public ResponseEntity<ApiResponse<SettlementResponse>> completeSettlement(
        @PathVariable Long settlementId,
        @AuthenticationPrincipal UserDetails userDetails) {

        SettlementResponse response = settlementService.completeSettlement(settlementId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response, "정산 완료 처리되었습니다."));
    }

    @PostMapping("/{settlementId}/confirm")
    public ResponseEntity<ApiResponse<SettlementResponse>> confirmSettlement(
        @PathVariable Long settlementId,
        @AuthenticationPrincipal UserDetails userDetails) {

        SettlementResponse response = settlementService.confirmSettlement(settlementId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response, "정산이 확인되었습니다."));
    }
}
