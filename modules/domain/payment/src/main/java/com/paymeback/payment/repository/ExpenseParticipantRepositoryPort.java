package com.paymeback.payment.repository;

import com.paymeback.payment.domain.ExpenseParticipant;

import java.util.List;
import java.util.Optional;

public interface ExpenseParticipantRepositoryPort {
    ExpenseParticipant save(ExpenseParticipant participant);
    List<ExpenseParticipant> saveAll(List<ExpenseParticipant> participants);
    Optional<ExpenseParticipant> findById(Long id);
    List<ExpenseParticipant> findByExpenseId(Long expenseId);
    List<ExpenseParticipant> findByUserId(Long userId);
    void deleteByExpenseId(Long expenseId);
}
