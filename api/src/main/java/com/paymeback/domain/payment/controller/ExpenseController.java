package com.paymeback.domain.payment.controller;

import com.paymeback.common.response.ApiResponse;
import com.paymeback.domain.payment.dto.CreateExpenseRequest;
import com.paymeback.domain.payment.dto.ExpenseResponse;
import com.paymeback.domain.payment.service.ExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Expense", description = "지출 API")
@RestController
@RequestMapping("/api/v1/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @Operation(summary = "지출 등록", description = "새로운 지출을 등록합니다.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "등록 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "모임을 찾을 수 없음")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<ExpenseResponse>> createExpense(
        @Valid @RequestBody CreateExpenseRequest request,
        @AuthenticationPrincipal UserDetails userDetails) {

        ExpenseResponse response = expenseService.createExpense(request, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response, "지출이 등록되었습니다."));
    }

    @Operation(summary = "지출 조회", description = "특정 지출의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "지출을 찾을 수 없음")
    })
    @GetMapping("/{expenseId}")
    public ResponseEntity<ApiResponse<ExpenseResponse>> getExpense(
        @Parameter(description = "지출 ID") @PathVariable Long expenseId,
        @AuthenticationPrincipal UserDetails userDetails) {

        ExpenseResponse response = expenseService.getExpense(expenseId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "모임별 지출 목록", description = "특정 모임의 모든 지출을 조회합니다.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "모임을 찾을 수 없음")
    })
    @GetMapping("/gathering/{gatheringId}")
    public ResponseEntity<ApiResponse<List<ExpenseResponse>>> getExpensesByGathering(
        @Parameter(description = "모임 ID") @PathVariable Long gatheringId,
        @AuthenticationPrincipal UserDetails userDetails) {

        List<ExpenseResponse> response = expenseService.getExpensesByGathering(gatheringId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "지출 삭제", description = "지출을 삭제합니다.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "삭제 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "지출을 찾을 수 없음")
    })
    @DeleteMapping("/{expenseId}")
    public ResponseEntity<ApiResponse<Void>> deleteExpense(
        @Parameter(description = "지출 ID") @PathVariable Long expenseId,
        @AuthenticationPrincipal UserDetails userDetails) {

        expenseService.deleteExpense(expenseId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("지출이 삭제되었습니다."));
    }
}
