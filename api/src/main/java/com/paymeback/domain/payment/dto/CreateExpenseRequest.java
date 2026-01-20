package com.paymeback.domain.payment.dto;

import com.paymeback.payment.domain.ExpenseCategory;
import com.paymeback.payment.domain.ShareType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

public record CreateExpenseRequest(

    @NotNull(message = "모임 ID는 필수입니다.")
    Long gatheringId,

    @NotNull(message = "총 금액은 필수입니다.")
    @Positive(message = "총 금액은 0보다 커야 합니다.")
    BigDecimal totalAmount,

    @Size(max = 200, message = "설명은 200자 이하여야 합니다.")
    String description,

    @Size(max = 100, message = "장소는 100자 이하여야 합니다.")
    String location,

    @NotNull(message = "카테고리는 필수입니다.")
    ExpenseCategory category,

    Long paidAt,

    String receiptImageUrl,

    @NotNull(message = "분담 타입은 필수입니다.")
    ShareType shareType,

    @NotEmpty(message = "참여자 목록은 필수입니다.")
    @Valid
    List<ParticipantShare> participants
) {

    public record ParticipantShare(
        @NotNull(message = "참여자 ID는 필수입니다.")
        Long userId,

        BigDecimal shareValue
    ) {
    }
}
