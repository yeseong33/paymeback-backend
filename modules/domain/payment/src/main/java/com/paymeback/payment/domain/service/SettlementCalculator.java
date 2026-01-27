package com.paymeback.payment.domain.service;

import com.paymeback.payment.domain.Expense;
import com.paymeback.payment.domain.ExpenseParticipant;
import com.paymeback.payment.domain.Settlement;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class SettlementCalculator {

    public List<Settlement> calculate(
        Long gatheringId,
        List<Expense> expenses,
        List<ExpenseParticipant> participants
    ) {
        Map<Long, BigDecimal> balances = calculateBalances(expenses, participants);
        return generateSettlements(gatheringId, balances);
    }

    private Map<Long, BigDecimal> calculateBalances(
        List<Expense> expenses,
        List<ExpenseParticipant> participants
    ) {
        Map<Long, BigDecimal> balances = new HashMap<>();

        for (Expense expense : expenses) {
            balances.merge(
                expense.payerId(),
                expense.totalAmount(),
                BigDecimal::add
            );
        }

        for (ExpenseParticipant participant : participants) {
            balances.merge(
                participant.userId(),
                participant.shareAmount().negate(),
                BigDecimal::add
            );
        }

        return balances;
    }

    private List<Settlement> generateSettlements(
        Long gatheringId,
        Map<Long, BigDecimal> balances
    ) {
        List<Settlement> settlements = new ArrayList<>();

        PriorityQueue<Map.Entry<Long, BigDecimal>> creditors = new PriorityQueue<>(
            Comparator.comparing(Map.Entry<Long, BigDecimal>::getValue).reversed()
        );
        PriorityQueue<Map.Entry<Long, BigDecimal>> debtors = new PriorityQueue<>(
            Comparator.comparing(Map.Entry::getValue)
        );

        for (Map.Entry<Long, BigDecimal> entry : balances.entrySet()) {
            if (entry.getValue().compareTo(BigDecimal.ZERO) > 0) {
                creditors.add(entry);
            } else if (entry.getValue().compareTo(BigDecimal.ZERO) < 0) {
                debtors.add(entry);
            }
        }

        while (!creditors.isEmpty() && !debtors.isEmpty()) {
            Map.Entry<Long, BigDecimal> creditor = creditors.poll();
            Map.Entry<Long, BigDecimal> debtor = debtors.poll();

            BigDecimal credit = creditor.getValue();
            BigDecimal debt = debtor.getValue().abs();

            BigDecimal settlementAmount = roundToTen(credit.min(debt));

            if (settlementAmount.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            settlements.add(Settlement.create(
                gatheringId,
                debtor.getKey(),
                creditor.getKey(),
                settlementAmount
            ));

            BigDecimal remainingCredit = credit.subtract(settlementAmount);
            BigDecimal remainingDebt = debt.subtract(settlementAmount);

            if (remainingCredit.compareTo(BigDecimal.ZERO) > 0) {
                creditors.add(Map.entry(creditor.getKey(), remainingCredit));
            }
            if (remainingDebt.compareTo(BigDecimal.ZERO) > 0) {
                debtors.add(Map.entry(debtor.getKey(), remainingDebt.negate()));
            }
        }

        return settlements;
    }

    private BigDecimal roundToTen(BigDecimal amount) {
        return amount.divide(BigDecimal.TEN, 0, RoundingMode.HALF_UP).multiply(BigDecimal.TEN);
    }
}
