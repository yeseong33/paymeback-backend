package com.paymeback.domain.payment.service;

import com.paymeback.common.exception.BusinessException;
import com.paymeback.common.exception.ErrorCode;
import com.paymeback.domain.gathering.service.GatheringService;
import com.paymeback.domain.payment.dto.CreateExpenseRequest;
import com.paymeback.domain.payment.dto.ExpenseResponse;
import com.paymeback.domain.user.service.UserService;
import com.paymeback.payment.domain.Expense;
import com.paymeback.payment.domain.ExpenseParticipant;
import com.paymeback.payment.domain.ExpenseParticipants;
import com.paymeback.payment.domain.ExpenseParticipants.ParticipantShare;
import com.paymeback.payment.repository.ExpenseParticipantRepositoryPort;
import com.paymeback.payment.repository.ExpenseRepositoryPort;
import com.paymeback.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExpenseService {

    private final ExpenseRepositoryPort expenseRepository;
    private final ExpenseParticipantRepositoryPort participantRepository;
    private final GatheringService gatheringService;
    private final UserService userService;

    @Transactional
    public ExpenseResponse createExpense(CreateExpenseRequest request, String userEmail) {
        // 검증
        User payer = userService.findByEmail(userEmail);
        gatheringService.existById(request.gatheringId());

        // 생성 및 저장
        Instant paidAt = request.paidAt() != null ? Instant.ofEpochMilli(request.paidAt()) : Instant.now();

        Expense savedExpense = expenseRepository.save(Expense.create(
            request.gatheringId(),
            payer.id(),
            request.totalAmount(),
            request.description(),
            request.location(),
            request.category(),
            paidAt,
            request.receiptImageUrl()
        ));
        log.info("지출이 생성되었습니다. id: {}, gathering: {}", savedExpense.id(), request.gatheringId());

        List<ParticipantShare> shares = request.participants().stream()
            .map(ps -> new ParticipantShare(ps.userId(), ps.shareValue()))
            .toList();

        ExpenseParticipants participants = ExpenseParticipants.distribute(
            savedExpense.id(),
            request.totalAmount(),
            request.shareType(),
            shares
        );

        List<ExpenseParticipant> savedParticipants = participantRepository.saveAll(participants.toList());

        // 응답 조립: 저장된 참여자 기준으로 유저 조회
        List<User> participantUsers = savedParticipants.stream()
            .map(p -> userService.findById(p.userId()))
            .toList();

        return ExpenseResponse.from(savedExpense, payer, savedParticipants, participantUsers);
    }

    public ExpenseResponse getExpense(Long expenseId, String userEmail) {
        userService.findByEmail(userEmail);

        Expense expense = findById(expenseId);
        User payer = userService.findById(expense.payerId());
        List<ExpenseParticipant> participants = participantRepository.findByExpenseId(expenseId);
        List<User> participantUsers = participants.stream()
            .map(p -> userService.findById(p.userId()))
            .toList();

        return ExpenseResponse.from(expense, payer, participants, participantUsers);
    }

    public List<ExpenseResponse> getExpensesByGathering(Long gatheringId, String userEmail) {
        userService.findByEmail(userEmail);

        List<Expense> expenses = expenseRepository.findByGatheringId(gatheringId);

        return expenses.stream()
            .map(expense -> {
                User payer = userService.findById(expense.payerId());
                List<ExpenseParticipant> participants = participantRepository.findByExpenseId(expense.id());
                List<User> participantUsers = participants.stream()
                    .map(p -> userService.findById(p.userId()))
                    .toList();
                return ExpenseResponse.from(expense, payer, participants, participantUsers);
            })
            .toList();
    }

    @Transactional
    public void deleteExpense(Long expenseId, String userEmail) {
        User user = userService.findByEmail(userEmail);
        Expense expense = findById(expenseId);

        if (!expense.payerId().equals(user.id())) {
            throw new BusinessException(ErrorCode.NOT_EXPENSE_OWNER);
        }

        participantRepository.deleteByExpenseId(expenseId);
        expenseRepository.deleteById(expenseId);
        log.info("지출이 삭제되었습니다. id: {}", expenseId);
    }

    public Expense findById(Long id) {
        return expenseRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.EXPENSE_NOT_FOUND));
    }

}
