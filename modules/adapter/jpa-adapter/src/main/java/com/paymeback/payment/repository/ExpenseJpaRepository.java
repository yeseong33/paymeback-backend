package com.paymeback.payment.repository;

import com.paymeback.payment.entity.ExpenseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseJpaRepository extends JpaRepository<ExpenseEntity, Long> {

    List<ExpenseEntity> findByGatheringIdOrderByPaidAtDesc(Long gatheringId);

    List<ExpenseEntity> findByPayerIdOrderByPaidAtDesc(Long payerId);
}
