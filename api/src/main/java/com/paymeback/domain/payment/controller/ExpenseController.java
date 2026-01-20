package com.paymeback.domain.payment.controller;

import com.paymeback.common.response.ApiResponse;
import com.paymeback.domain.payment.dto.CreateExpenseRequest;
import com.paymeback.domain.payment.dto.ExpenseResponse;
import com.paymeback.domain.payment.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<ApiResponse<ExpenseResponse>> createExpense(
        @Valid @RequestBody CreateExpenseRequest request,
        @AuthenticationPrincipal UserDetails userDetails) {

        ExpenseResponse response = expenseService.createExpense(request, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response, "지출이 등록되었습니다."));
    }

    @GetMapping("/{expenseId}")
    public ResponseEntity<ApiResponse<ExpenseResponse>> getExpense(
        @PathVariable Long expenseId,
        @AuthenticationPrincipal UserDetails userDetails) {

        ExpenseResponse response = expenseService.getExpense(expenseId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/gathering/{gatheringId}")
    public ResponseEntity<ApiResponse<List<ExpenseResponse>>> getExpensesByGathering(
        @PathVariable Long gatheringId,
        @AuthenticationPrincipal UserDetails userDetails) {

        List<ExpenseResponse> response = expenseService.getExpensesByGathering(gatheringId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{expenseId}")
    public ResponseEntity<ApiResponse<Void>> deleteExpense(
        @PathVariable Long expenseId,
        @AuthenticationPrincipal UserDetails userDetails) {

        expenseService.deleteExpense(expenseId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("지출이 삭제되었습니다."));
    }
}
