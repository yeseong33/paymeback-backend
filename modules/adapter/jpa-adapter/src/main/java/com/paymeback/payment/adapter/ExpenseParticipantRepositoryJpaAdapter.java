package com.paymeback.payment.adapter;

import com.paymeback.payment.domain.ExpenseParticipant;
import com.paymeback.payment.repository.ExpenseJpaRepository;
import com.paymeback.payment.repository.ExpenseParticipantJpaRepository;
import com.paymeback.payment.repository.ExpenseParticipantRepositoryPort;
import com.paymeback.user.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ExpenseParticipantRepositoryJpaAdapter implements ExpenseParticipantRepositoryPort {

    private final ExpenseParticipantJpaRepository participantRepo;
    private final ExpenseJpaRepository expenseRepo;
    private final UserJpaRepository userRepo;

    @Override
    public ExpenseParticipant save(ExpenseParticipant participant) {
        var expense = expenseRepo.findById(participant.expenseId())
            .orElseThrow(() -> new IllegalArgumentException("Expense not found: " + participant.expenseId()));
        var user = userRepo.findById(participant.userId())
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + participant.userId()));
        var entity = ExpenseParticipantJpaMapper.toEntity(participant, expense, user);
        var saved = participantRepo.save(entity);
        return ExpenseParticipantJpaMapper.toDomain(saved);
    }

    @Override
    public List<ExpenseParticipant> saveAll(List<ExpenseParticipant> participants) {
        var entities = participants.stream()
            .map(p -> {
                var expense = expenseRepo.findById(p.expenseId())
                    .orElseThrow(() -> new IllegalArgumentException("Expense not found: " + p.expenseId()));
                var user = userRepo.findById(p.userId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + p.userId()));
                return ExpenseParticipantJpaMapper.toEntity(p, expense, user);
            })
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
        return participantRepo.findByExpense_Id(expenseId).stream()
            .map(ExpenseParticipantJpaMapper::toDomain)
            .toList();
    }

    @Override
    public List<ExpenseParticipant> findByUserId(Long userId) {
        return participantRepo.findByUser_Id(userId).stream()
            .map(ExpenseParticipantJpaMapper::toDomain)
            .toList();
    }

    @Override
    @Transactional
    public void deleteByExpenseId(Long expenseId) {
        participantRepo.deleteByExpense_Id(expenseId);
    }
}
