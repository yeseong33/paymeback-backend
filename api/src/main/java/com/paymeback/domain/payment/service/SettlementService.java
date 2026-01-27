package com.paymeback.domain.payment.service;

import com.paymeback.common.exception.BusinessException;
import com.paymeback.common.exception.ErrorCode;
import com.paymeback.domain.payment.dto.SettlementResponse;
import com.paymeback.domain.user.service.UserService;
import com.paymeback.gathering.repository.GatheringRepositoryPort;
import com.paymeback.payment.domain.Expense;
import com.paymeback.payment.domain.ExpenseParticipant;
import com.paymeback.payment.domain.Settlement;
import com.paymeback.payment.domain.SettlementStatus;
import com.paymeback.payment.domain.service.SettlementCalculator;
import com.paymeback.payment.repository.ExpenseParticipantRepositoryPort;
import com.paymeback.payment.repository.ExpenseRepositoryPort;
import com.paymeback.payment.repository.SettlementRepositoryPort;
import com.paymeback.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SettlementService {

    private final SettlementRepositoryPort settlementRepository;
    private final ExpenseRepositoryPort expenseRepository;
    private final ExpenseParticipantRepositoryPort participantRepository;
    private final GatheringRepositoryPort gatheringRepository;
    private final UserService userService;
    private final SettlementCalculator settlementCalculator = new SettlementCalculator();

    @Transactional
    public List<SettlementResponse> calculateSettlements(Long gatheringId, String userEmail) {
        userService.findByEmail(userEmail);

        gatheringRepository.findById(gatheringId)
            .orElseThrow(() -> new BusinessException(ErrorCode.GATHERING_NOT_FOUND));

        List<Settlement> completedSettlements = settlementRepository.findByGatheringIdAndStatus(gatheringId, SettlementStatus.COMPLETED);
        List<Settlement> confirmedSettlements = settlementRepository.findByGatheringIdAndStatus(gatheringId, SettlementStatus.CONFIRMED);
        if (!completedSettlements.isEmpty() || !confirmedSettlements.isEmpty()) {
            throw new BusinessException(ErrorCode.SETTLEMENT_IN_PROGRESS);
        }

        settlementRepository.deleteByGatheringId(gatheringId);

        List<Expense> expenses = expenseRepository.findByGatheringId(gatheringId);
        List<ExpenseParticipant> allParticipants = new ArrayList<>();
        for (Expense expense : expenses) {
            allParticipants.addAll(participantRepository.findByExpenseId(expense.id()));
        }

        List<Settlement> settlements = settlementCalculator.calculate(gatheringId, expenses, allParticipants);
        List<Settlement> savedSettlements = settlementRepository.saveAll(settlements);

        log.info("정산이 계산되었습니다. gathering: {}, settlements: {}", gatheringId, savedSettlements.size());

        return savedSettlements.stream()
            .map(s -> {
                User fromUser = userService.findById(s.fromUserId());
                User toUser = userService.findById(s.toUserId());
                return SettlementResponse.from(s, fromUser, toUser);
            })
            .toList();
    }

    public List<SettlementResponse> getSettlementsByGathering(Long gatheringId, String userEmail) {
        userService.findByEmail(userEmail);

        List<Settlement> settlements = settlementRepository.findByGatheringId(gatheringId);

        return settlements.stream()
            .map(s -> {
                User fromUser = userService.findById(s.fromUserId());
                User toUser = userService.findById(s.toUserId());
                return SettlementResponse.from(s, fromUser, toUser);
            })
            .toList();
    }

    public List<SettlementResponse> getMySettlementsToSend(String userEmail) {
        User user = userService.findByEmail(userEmail);

        List<Settlement> settlements = settlementRepository.findByFromUserId(user.id());

        return settlements.stream()
            .map(s -> {
                User fromUser = userService.findById(s.fromUserId());
                User toUser = userService.findById(s.toUserId());
                return SettlementResponse.from(s, fromUser, toUser);
            })
            .toList();
    }

    public List<SettlementResponse> getMySettlementsToReceive(String userEmail) {
        User user = userService.findByEmail(userEmail);

        List<Settlement> settlements = settlementRepository.findByToUserId(user.id());

        return settlements.stream()
            .map(s -> {
                User fromUser = userService.findById(s.fromUserId());
                User toUser = userService.findById(s.toUserId());
                return SettlementResponse.from(s, fromUser, toUser);
            })
            .toList();
    }

    @Transactional
    public SettlementResponse completeSettlement(Long settlementId, String userEmail) {
        User user = userService.findByEmail(userEmail);
        Settlement settlement = findById(settlementId);

        if (!settlement.fromUserId().equals(user.id())) {
            throw new BusinessException(ErrorCode.NOT_SETTLEMENT_SENDER);
        }

        if (settlement.status() != SettlementStatus.PENDING) {
            throw new BusinessException(ErrorCode.SETTLEMENT_ALREADY_PROCESSED);
        }

        Settlement completedSettlement = settlement.complete();
        Settlement savedSettlement = settlementRepository.save(completedSettlement);

        log.info("정산이 완료되었습니다. id: {}", settlementId);

        User fromUser = userService.findById(savedSettlement.fromUserId());
        User toUser = userService.findById(savedSettlement.toUserId());
        return SettlementResponse.from(savedSettlement, fromUser, toUser);
    }

    @Transactional
    public SettlementResponse confirmSettlement(Long settlementId, String userEmail) {
        User user = userService.findByEmail(userEmail);
        Settlement settlement = findById(settlementId);

        if (!settlement.toUserId().equals(user.id())) {
            throw new BusinessException(ErrorCode.NOT_SETTLEMENT_RECEIVER);
        }

        if (settlement.status() != SettlementStatus.COMPLETED) {
            throw new BusinessException(ErrorCode.SETTLEMENT_NOT_COMPLETED);
        }

        Settlement confirmedSettlement = settlement.confirm();
        Settlement savedSettlement = settlementRepository.save(confirmedSettlement);

        log.info("정산이 확인되었습니다. id: {}", settlementId);

        User fromUser = userService.findById(savedSettlement.fromUserId());
        User toUser = userService.findById(savedSettlement.toUserId());
        return SettlementResponse.from(savedSettlement, fromUser, toUser);
    }

    public Settlement findById(Long id) {
        return settlementRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.SETTLEMENT_NOT_FOUND));
    }
}
