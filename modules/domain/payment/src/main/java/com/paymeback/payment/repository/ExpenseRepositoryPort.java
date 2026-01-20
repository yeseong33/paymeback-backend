package com.paymeback.payment.repository;

import com.paymeback.payment.domain.Expense;

import java.util.List;
import java.util.Optional;

public interface ExpenseRepositoryPort {
    Expense save(Expense expense);
    Optional<Expense> findById(Long id);
    List<Expense> findByGatheringId(Long gatheringId);
    List<Expense> findByPayerId(Long payerId);
    void deleteById(Long id);
}
