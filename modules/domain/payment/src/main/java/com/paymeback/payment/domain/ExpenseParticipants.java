package com.paymeback.payment.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExpenseParticipants {

    private static final BigDecimal TEN = BigDecimal.TEN;

    private final List<ExpenseParticipant> values;

    private ExpenseParticipants(List<ExpenseParticipant> values) {
        this.values = new ArrayList<>(values);
    }

    public static ExpenseParticipants distribute(
        Long expenseId,
        BigDecimal totalAmount,
        ShareType shareType,
        List<ParticipantShare> shares
    ) {
        List<ExpenseParticipant> participants = switch (shareType) {
            case EQUAL -> createEqual(expenseId, totalAmount, shares);
            case CUSTOM -> createCustom(expenseId, shares);
            case PERCENTAGE -> createPercentage(expenseId, totalAmount, shares);
        };
        return new ExpenseParticipants(participants);
    }

    public List<ExpenseParticipant> toList() {
        return Collections.unmodifiableList(values);
    }

    public int size() {
        return values.size();
    }

    private static List<ExpenseParticipant> createEqual(
        Long expenseId,
        BigDecimal totalAmount,
        List<ParticipantShare> shares
    ) {
        List<ExpenseParticipant> participants = new ArrayList<>();
        int count = shares.size();

        BigDecimal shareAmount = roundDownToTen(
            totalAmount.divide(BigDecimal.valueOf(count), 0, RoundingMode.DOWN)
        );
        BigDecimal allocated = shareAmount.multiply(BigDecimal.valueOf(count - 1));
        BigDecimal lastShareAmount = totalAmount.subtract(allocated);

        for (int i = 0; i < count; i++) {
            BigDecimal amount = (i == count - 1) ? lastShareAmount : shareAmount;
            participants.add(ExpenseParticipant.create(
                expenseId,
                shares.get(i).userId(),
                amount,
                ShareType.EQUAL,
                null
            ));
        }
        return participants;
    }

    private static List<ExpenseParticipant> createCustom(
        Long expenseId,
        List<ParticipantShare> shares
    ) {
        List<ExpenseParticipant> participants = new ArrayList<>();
        for (ParticipantShare ps : shares) {
            BigDecimal shareAmount = ps.shareValue() != null ? ps.shareValue() : BigDecimal.ZERO;
            participants.add(ExpenseParticipant.create(
                expenseId,
                ps.userId(),
                shareAmount,
                ShareType.CUSTOM,
                shareAmount
            ));
        }
        return participants;
    }

    private static List<ExpenseParticipant> createPercentage(
        Long expenseId,
        BigDecimal totalAmount,
        List<ParticipantShare> shares
    ) {
        List<ExpenseParticipant> participants = new ArrayList<>();
        for (ParticipantShare ps : shares) {
            BigDecimal percentage = ps.shareValue() != null ? ps.shareValue() : BigDecimal.ZERO;
            BigDecimal shareAmount = roundToTen(
                totalAmount.multiply(percentage)
                    .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP)
            );
            participants.add(ExpenseParticipant.create(
                expenseId,
                ps.userId(),
                shareAmount,
                ShareType.PERCENTAGE,
                percentage
            ));
        }
        return participants;
    }

    private static BigDecimal roundDownToTen(BigDecimal amount) {
        return amount.divide(TEN, 0, RoundingMode.DOWN).multiply(TEN);
    }

    private static BigDecimal roundToTen(BigDecimal amount) {
        return amount.divide(TEN, 0, RoundingMode.HALF_UP).multiply(TEN);
    }

    public record ParticipantShare(Long userId, BigDecimal shareValue) {
    }
}