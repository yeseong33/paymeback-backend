package com.paymeback.domain.payment.service;

import com.paymeback.common.exception.BusinessException;
import com.paymeback.common.exception.ErrorCode;
import com.paymeback.domain.payment.dto.CreateExpenseRequest;
import com.paymeback.domain.payment.dto.ExpenseResponse;
import com.paymeback.domain.user.service.UserService;
import com.paymeback.gathering.repository.GatheringRepositoryPort;
import com.paymeback.payment.domain.Expense;
import com.paymeback.payment.domain.ExpenseParticipant;
import com.paymeback.payment.domain.ShareType;
import com.paymeback.payment.repository.ExpenseParticipantRepositoryPort;
import com.paymeback.payment.repository.ExpenseRepositoryPort;
import com.paymeback.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExpenseService {

    private final ExpenseRepositoryPort expenseRepository;
    private final ExpenseParticipantRepositoryPort participantRepository;
    private final GatheringRepositoryPort gatheringRepository;
    private final UserService userService;

    @Transactional
    public ExpenseResponse createExpense(CreateExpenseRequest request, String userEmail) {
        User payer = userService.findByEmail(userEmail);

        gatheringRepository.findById(request.gatheringId())
            .orElseThrow(() -> new BusinessException(ErrorCode.GATHERING_NOT_FOUND));

        Instant paidAt = request.paidAt() != null ? Instant.ofEpochMilli(request.paidAt()) : Instant.now();

        Expense expense = Expense.create(
            request.gatheringId(),
            payer.id(),
            request.totalAmount(),
            request.description(),
            request.location(),
            request.category(),
            paidAt,
            request.receiptImageUrl()
        );

        Expense savedExpense = expenseRepository.save(expense);
        log.info("지출이 생성되었습니다. id: {}, gathering: {}", savedExpense.id(), request.gatheringId());

        List<ExpenseParticipant> participants = createParticipants(
            savedExpense.id(),
            request.totalAmount(),
            request.shareType(),
            request.participants()
        );

        List<ExpenseParticipant> savedParticipants = participantRepository.saveAll(participants);

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

    private List<ExpenseParticipant> createParticipants(
        Long expenseId,
        BigDecimal totalAmount,
        ShareType shareType,
        List<CreateExpenseRequest.ParticipantShare> participantShares
    ) {
        List<ExpenseParticipant> participants = new ArrayList<>();
        int participantCount = participantShares.size();

        switch (shareType) {
            case EQUAL -> {
                BigDecimal shareAmount = totalAmount.divide(
                    BigDecimal.valueOf(participantCount),
                    2,
                    RoundingMode.HALF_UP
                );
                for (CreateExpenseRequest.ParticipantShare ps : participantShares) {
                    participants.add(ExpenseParticipant.create(
                        expenseId,
                        ps.userId(),
                        shareAmount,
                        ShareType.EQUAL,
                        null
                    ));
                }
            }
            case CUSTOM -> {
                for (CreateExpenseRequest.ParticipantShare ps : participantShares) {
                    BigDecimal shareAmount = ps.shareValue() != null ? ps.shareValue() : BigDecimal.ZERO;
                    participants.add(ExpenseParticipant.create(
                        expenseId,
                        ps.userId(),
                        shareAmount,
                        ShareType.CUSTOM,
                        shareAmount
                    ));
                }
            }
            case PERCENTAGE -> {
                for (CreateExpenseRequest.ParticipantShare ps : participantShares) {
                    BigDecimal percentage = ps.shareValue() != null ? ps.shareValue() : BigDecimal.ZERO;
                    BigDecimal shareAmount = totalAmount
                        .multiply(percentage)
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                    participants.add(ExpenseParticipant.create(
                        expenseId,
                        ps.userId(),
                        shareAmount,
                        ShareType.PERCENTAGE,
                        percentage
                    ));
                }
            }
        }

        return participants;
    }
}
