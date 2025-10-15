package com.bank.webApplication.Orchestrator;

import com.bank.webApplication.Dto.DepositWithdrawDTO;
import com.bank.webApplication.Entity.LogEntity;
import com.bank.webApplication.Entity.TransactionEntity;
import com.bank.webApplication.Services.LogService;
import com.bank.webApplication.Services.TransactionService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Controller
@Component
public class DepositAndWithdrawalOrch {
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private LogService logService;

    @Autowired
    public DepositAndWithdrawalOrch(TransactionService transactionService, LogService logService) {
        this.transactionService = transactionService;
        this.logService = logService;
    }

    @Transactional
    public DepositWithdrawDTO depositHandler(UUID userId, String accountNumber, double amount) {
        log.info("[DepositAndWithdrawalOrch] depositHandler entered SUCCESS");
        if (userId == null) {
            log.error("[DepositAndWithdrawalOrch] depositHandler: User ID cannot be null  FAILURE");
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (accountNumber == null || accountNumber.isBlank()) {
            log.error("[DepositAndWithdrawalOrch] depositHandler: Account number cannot be null or empty  FAILURE");
            throw new IllegalArgumentException("Account number cannot be null or empty");
        }

        DepositWithdrawDTO depositOperation = transactionService.depositAmount(accountNumber, amount);
        log.info("[DepositAndWithdrawalOrch]  Deposit Successful" + " " + depositOperation.getStatus() + " ");
        if (depositOperation != null && depositOperation.getStatus() == TransactionEntity.status.COMPLETED) {
            transactionService.saveTransaction(
                    null,
                    accountNumber,
                    amount,
                    "Deposit Transaction Save Successful :)",
                    TransactionEntity.type.DEPOSIT,
                    TransactionEntity.status.COMPLETED
            );
            log.info("[DepositAndWithdrawalOrch] Deposit Operation Successful :) ");
            logService.logintoDB(userId, LogEntity.Action.TRANSACTIONS, "Deposit Successful", String.valueOf(userId), LogEntity.Status.SUCCESS);
            log.info("[DepositAndWithdrawalOrch] Transaction Saved Successfully in Logs");
        } else {
            log.error("[DepositAndWithdrawalOrch] Deposit Operation Failed. Please Try Again");
            logService.logintoDB(userId, LogEntity.Action.TRANSACTIONS, "Deposit Failed", String.valueOf(userId), LogEntity.Status.FAILURE);
        }
        return depositOperation;
    }

    @Transactional
    public DepositWithdrawDTO WithdrawalHandler(UUID userId, String accountNumber, double amount) {
        log.info("[DepositAndWithdrawalOrch]  WithdrawalHandler entered SUCCESS");
        if (userId == null) {
            log.error("[DepositAndWithdrawalOrch] WithdrawalHandler: User ID cannot be null  FAILURE");
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (accountNumber == null || accountNumber.isBlank()) {
            log.error("[DepositAndWithdrawalOrch] WithdrawalHandler: Account number cannot be null or empty  FAILURE");
            throw new IllegalArgumentException("Account number cannot be null or empty");
        }

        DepositWithdrawDTO withdrawOperation = transactionService.withdrawAmount(accountNumber, amount);
        if (withdrawOperation != null && withdrawOperation.getStatus() == TransactionEntity.status.COMPLETED) {
            transactionService.saveTransaction(
                    accountNumber,
                    null,
                    amount,
                    "Withdrawal Transaction save Successful :)",
                    TransactionEntity.type.WITHDRAWAL,
                    TransactionEntity.status.COMPLETED
            );
            log.info("[DepositAndWithdrawalOrch] Withdraw Operation Successful :)");
            logService.logintoDB(userId, LogEntity.Action.TRANSACTIONS, "Withdrawal Successful", String.valueOf(userId), LogEntity.Status.SUCCESS);
            log.info("[DepositAndWithdrawalOrch] Transaction Saved Successfully in Logs");
        } else {
            log.error("[DepositAndWithdrawalOrch] Withdraw Operation Failed. Please Try Again");
            logService.logintoDB(userId, LogEntity.Action.TRANSACTIONS, "Withdrawal Failed", String.valueOf(userId), LogEntity.Status.FAILURE);
        }
        return withdrawOperation;
    }
}