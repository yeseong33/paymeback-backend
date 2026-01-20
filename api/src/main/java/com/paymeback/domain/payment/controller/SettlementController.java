package com.paymeback.domain.payment.controller;

import com.paymeback.common.response.ApiResponse;
import com.paymeback.domain.payment.dto.SettlementResponse;
import com.paymeback.domain.payment.service.SettlementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Settlement", description = "정산 API")
@RestController
@RequestMapping("/api/v1/settlements")
@RequiredArgsConstructor
public class SettlementController {

    private final SettlementService settlementService;

    @Operation(summary = "정산 계산", description = "모임의 정산 금액을 계산합니다.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "계산 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "모임을 찾을 수 없음")
    })
    @PostMapping("/calculate/{gatheringId}")
    public ResponseEntity<ApiResponse<List<SettlementResponse>>> calculateSettlements(
        @Parameter(description = "모임 ID") @PathVariable Long gatheringId,
        @AuthenticationPrincipal UserDetails userDetails) {

        List<SettlementResponse> response = settlementService.calculateSettlements(gatheringId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response, "정산이 계산되었습니다."));
    }

    @Operation(summary = "모임별 정산 목록", description = "특정 모임의 모든 정산 내역을 조회합니다.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "모임을 찾을 수 없음")
    })
    @GetMapping("/gathering/{gatheringId}")
    public ResponseEntity<ApiResponse<List<SettlementResponse>>> getSettlementsByGathering(
        @Parameter(description = "모임 ID") @PathVariable Long gatheringId,
        @AuthenticationPrincipal UserDetails userDetails) {

        List<SettlementResponse> response = settlementService.getSettlementsByGathering(gatheringId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "보내야 할 정산 목록", description = "내가 보내야 할 정산 목록을 조회합니다.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/my/to-send")
    public ResponseEntity<ApiResponse<List<SettlementResponse>>> getMySettlementsToSend(
        @AuthenticationPrincipal UserDetails userDetails) {

        List<SettlementResponse> response = settlementService.getMySettlementsToSend(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "받아야 할 정산 목록", description = "내가 받아야 할 정산 목록을 조회합니다.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/my/to-receive")
    public ResponseEntity<ApiResponse<List<SettlementResponse>>> getMySettlementsToReceive(
        @AuthenticationPrincipal UserDetails userDetails) {

        List<SettlementResponse> response = settlementService.getMySettlementsToReceive(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "정산 완료", description = "정산을 완료 처리합니다. (송금자가 호출)")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "완료 처리 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "정산을 찾을 수 없음")
    })
    @PostMapping("/{settlementId}/complete")
    public ResponseEntity<ApiResponse<SettlementResponse>> completeSettlement(
        @Parameter(description = "정산 ID") @PathVariable Long settlementId,
        @AuthenticationPrincipal UserDetails userDetails) {

        SettlementResponse response = settlementService.completeSettlement(settlementId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response, "정산 완료 처리되었습니다."));
    }

    @Operation(summary = "정산 확인", description = "정산 수령을 확인합니다. (수령자가 호출)")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "확인 처리 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "정산을 찾을 수 없음")
    })
    @PostMapping("/{settlementId}/confirm")
    public ResponseEntity<ApiResponse<SettlementResponse>> confirmSettlement(
        @Parameter(description = "정산 ID") @PathVariable Long settlementId,
        @AuthenticationPrincipal UserDetails userDetails) {

        SettlementResponse response = settlementService.confirmSettlement(settlementId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response, "정산이 확인되었습니다."));
    }
}
