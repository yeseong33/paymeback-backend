package com.paymeback.payment.adapter;

import com.paymeback.payment.domain.Expense;
import com.paymeback.payment.entity.ExpenseEntity;
import com.paymeback.payment.repository.ExpenseJpaRepository;
import com.paymeback.payment.repository.ExpenseRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ExpenseRepositoryJpaAdapter implements ExpenseRepositoryPort {

    private final ExpenseJpaRepository expenseRepo;

    @Override
    public Expense save(Expense expense) {
        ExpenseEntity entity;

        if (expense.id() != null) {
            entity = expenseRepo.findById(expense.id())
                .orElseThrow(() -> new IllegalArgumentException("Expense not found: " + expense.id()));
            entity.updateFromDomain(
                expense.gatheringId(),
                expense.payerId(),
                expense.totalAmount(),
                expense.description(),
                expense.location(),
                expense.category(),
                expense.paidAt(),
                expense.receiptImageUrl()
            );
        } else {
            entity = ExpenseJpaMapper.toEntity(expense);
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
        return expenseRepo.findByGatheringIdOrderByPaidAtDesc(gatheringId).stream()
            .map(ExpenseJpaMapper::toDomain)
            .toList();
    }

    @Override
    public List<Expense> findByPayerId(Long payerId) {
        return expenseRepo.findByPayerIdOrderByPaidAtDesc(payerId).stream()
            .map(ExpenseJpaMapper::toDomain)
            .toList();
    }

    @Override
    public void deleteById(Long id) {
        expenseRepo.deleteById(id);
    }
}
