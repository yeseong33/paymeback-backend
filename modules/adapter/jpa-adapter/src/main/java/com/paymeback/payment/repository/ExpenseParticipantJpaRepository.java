package com.paymeback.payment.repository;

import com.paymeback.payment.entity.ExpenseParticipantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseParticipantJpaRepository extends JpaRepository<ExpenseParticipantEntity, Long> {

    List<ExpenseParticipantEntity> findByExpenseId(Long expenseId);

    List<ExpenseParticipantEntity> findByUserId(Long userId);

    void deleteByExpenseId(Long expenseId);
}
