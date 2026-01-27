package com.paymeback.payment.adapter;

import com.paymeback.gathering.repository.GatheringJpaRepository;
import com.paymeback.payment.domain.Expense;
import com.paymeback.payment.entity.ExpenseEntity;
import com.paymeback.payment.repository.ExpenseJpaRepository;
import com.paymeback.payment.repository.ExpenseRepositoryPort;
import com.paymeback.user.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ExpenseRepositoryJpaAdapter implements ExpenseRepositoryPort {

    private final ExpenseJpaRepository expenseRepo;
    private final GatheringJpaRepository gatheringRepo;
    private final UserJpaRepository userRepo;

    @Override
    public Expense save(Expense expense) {
        ExpenseEntity entity;

        if (expense.id() != null) {
            entity = expenseRepo.findById(expense.id())
                .orElseThrow(() -> new IllegalArgumentException("Expense not found: " + expense.id()));
            entity.updateFromDomain(
                expense.totalAmount(),
                expense.description(),
                expense.location(),
                expense.category(),
                expense.paidAt(),
                expense.receiptImageUrl()
            );
        } else {
            var gathering = gatheringRepo.findById(expense.gatheringId())
                .orElseThrow(() -> new IllegalArgumentException("Gathering not found: " + expense.gatheringId()));
            var payer = userRepo.findById(expense.payerId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + expense.payerId()));
            entity = ExpenseJpaMapper.toEntity(expense, gathering, payer);
        }

        var saved = expenseRepo.save(entity);
        return ExpenseJpaMapper.toDomain(saved);
    }

    @Override
    public Optional<Expense> findById(Long id) {
        return expenseRepo.findById(id).map(ExpenseJpaMapper::toDomain);
    }

    @Override
    public List<Expense> findByGatheringId(Long gatheringId) {
        return expenseRepo.findByGathering_IdOrderByPaidAtDesc(gatheringId).stream()
            .map(ExpenseJpaMapper::toDomain)
            .toList();
    }

    @Override
    public List<Expense> findByPayerId(Long payerId) {
        return expenseRepo.findByPayer_IdOrderByPaidAtDesc(payerId).stream()
            .map(ExpenseJpaMapper::toDomain)
            .toList();
    }

    @Override
    public void deleteById(Long id) {
        expenseRepo.deleteById(id);
    }
}
