package com.paymeback.domain.payment.dto;

import com.paymeback.domain.user.dto.UserResponse;
import com.paymeback.payment.domain.Expense;
import com.paymeback.payment.domain.ExpenseCategory;
import com.paymeback.payment.domain.ExpenseParticipant;
import com.paymeback.payment.domain.ShareType;
import com.paymeback.user.domain.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public record ExpenseResponse(
    Long id,
    Long gatheringId,
    UserResponse payer,
    BigDecimal totalAmount,
    String description,
    String location,
    ExpenseCategory category,
    Long paidAt,
    String receiptImageUrl,
    List<ParticipantShareResponse> participants,
    Long createdAt
) {

    public record ParticipantShareResponse(
        Long id,
        UserResponse user,
        BigDecimal shareAmount,
        ShareType shareType,
        BigDecimal shareValue
    ) {
        public static ParticipantShareResponse from(ExpenseParticipant participant, User user) {
            return new ParticipantShareResponse(
                participant.id(),
                UserResponse.from(user),
                participant.shareAmount(),
                participant.shareType(),
                participant.shareValue()
            );
        }
    }

    public static ExpenseResponse from(
        Expense expense,
        User payer,
        List<ExpenseParticipant> participants,
        List<User> participantUsers
    ) {
        Map<Long, User> userMap = participantUsers.stream()
            .collect(Collectors.toMap(User::id, Function.identity()));

        List<ParticipantShareResponse> participantResponses = participants.stream()
            .map(p -> ParticipantShareResponse.from(p, userMap.get(p.userId())))
            .toList();

        return new ExpenseResponse(
            expense.id(),
            expense.gatheringId(),
            UserResponse.from(payer),
            expense.totalAmount(),
            expense.description(),
            expense.location(),
            expense.category(),
            expense.paidAt() != null ? expense.paidAt().toEpochMilli() : null,
            expense.receiptImageUrl(),
            participantResponses,
            expense.createdAt() != null ? expense.createdAt().toEpochMilli() : null
        );
    }
}
