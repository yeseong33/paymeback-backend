package com.paymeback.payment.repository;

import com.paymeback.payment.entity.ExpenseParticipantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseParticipantJpaRepository extends JpaRepository<ExpenseParticipantEntity, Long> {

    List<ExpenseParticipantEntity> findByExpense_Id(Long expenseId);

    List<ExpenseParticipantEntity> findByUser_Id(Long userId);

    void deleteByExpense_Id(Long expenseId);
}
