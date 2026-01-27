package com.paymeback.payment.repository;

import com.paymeback.payment.entity.ExpenseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseJpaRepository extends JpaRepository<ExpenseEntity, Long> {

    List<ExpenseEntity> findByGathering_IdOrderByPaidAtDesc(Long gatheringId);

    List<ExpenseEntity> findByPayer_IdOrderByPaidAtDesc(Long payerId);
}
