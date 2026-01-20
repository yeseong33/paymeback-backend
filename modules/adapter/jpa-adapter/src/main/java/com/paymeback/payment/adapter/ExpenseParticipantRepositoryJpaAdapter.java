package com.paymeback.payment.adapter;

import com.paymeback.payment.domain.ExpenseParticipant;
import com.paymeback.payment.repository.ExpenseParticipantJpaRepository;
import com.paymeback.payment.repository.ExpenseParticipantRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ExpenseParticipantRepositoryJpaAdapter implements ExpenseParticipantRepositoryPort {

    private final ExpenseParticipantJpaRepository participantRepo;

    @Override
    public ExpenseParticipant save(ExpenseParticipant participant) {
        var entity = ExpenseParticipantJpaMapper.toEntity(participant);
        var saved = participantRepo.save(entity);
        return ExpenseParticipantJpaMapper.toDomain(saved);
    }

    @Override
    public List<ExpenseParticipant> saveAll(List<ExpenseParticipant> participants) {
        var entities = participants.stream()
            .map(ExpenseParticipantJpaMapper::toEntity)
            .toList();
        var saved = participantRepo.saveAll(entities);
        return saved.stream()
            .map(ExpenseParticipantJpaMapper::toDomain)
            .toList();
    }

    @Override
    public Optional<ExpenseParticipant> findById(Long id) {
        return participantRepo.findById(id).map(ExpenseParticipantJpaMapper::toDomain);
    }

    @Override
    public List<ExpenseParticipant> findByExpenseId(Long expenseId) {
        return participantRepo.findByExpenseId(expenseId).stream()
            .map(ExpenseParticipantJpaMapper::toDomain)
            .toList();
    }

    @Override
    public List<ExpenseParticipant> findByUserId(Long userId) {
        return participantRepo.findByUserId(userId).stream()
            .map(ExpenseParticipantJpaMapper::toDomain)
            .toList();
    }

    @Override
    @Transactional
    public void deleteByExpenseId(Long expenseId) {
        participantRepo.deleteByExpenseId(expenseId);
    }
}
