package com.bank.webApplication.Orchestrator;

import com.bank.webApplication.Dto.DepositWithdrawDTO;
import com.bank.webApplication.Entity.LogEntity;
import com.bank.webApplication.Entity.TransactionEntity;
import com.bank.webApplication.Services.LogService;
import com.bank.webApplication.Services.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DepositAndWithdrawalOrchTests {

        @Mock
        private TransactionService transactionService;

        @Mock
        private LogService logService;

        @InjectMocks
        private DepositAndWithdrawalOrch depositAndWithdrawalOrch;

        private UUID userId;
        private String accountNumber;
        private double amount;
        private DepositWithdrawDTO successDTO;
        private DepositWithdrawDTO failedDTO;

        @BeforeEach
        void setUp() {
            MockitoAnnotations.openMocks(this);
            userId = UUID.randomUUID();
            accountNumber = "ACC123";
            amount = 500;

            successDTO = new DepositWithdrawDTO(accountNumber, "Success", amount, TransactionEntity.status.COMPLETED, TransactionEntity.type.DEPOSIT);
            failedDTO = new DepositWithdrawDTO(accountNumber, "Failed", amount, TransactionEntity.status.FAILED, TransactionEntity.type.DEPOSIT);
        }

        /// Deposit Handler Tests

        @Test
        void depositHandler_success() {
            when(transactionService.depositAmount(accountNumber, amount)).thenReturn(successDTO);

            DepositWithdrawDTO result = depositAndWithdrawalOrch.depositHandler(userId, accountNumber, amount);

            assertThat(result).isNotNull();
            assertThat(result.getStatus()).isEqualTo(TransactionEntity.status.COMPLETED);

            verify(transactionService, times(1))
                    .saveTransaction(null, accountNumber, amount, "Deposit Transaction Save Successful :)", TransactionEntity.type.DEPOSIT, TransactionEntity.status.COMPLETED);
            verify(logService, times(1))
                    .logintoDB(userId, LogEntity.Action.TRANSACTIONS, "Deposit Successful", String.valueOf(userId), LogEntity.Status.SUCCESS);
        }

        @Test
        void depositHandler_failedTransaction_returnsFailedDTOAndLogsFailure() {
            when(transactionService.depositAmount(accountNumber, amount)).thenReturn(failedDTO);

            DepositWithdrawDTO result = depositAndWithdrawalOrch.depositHandler(userId, accountNumber, amount);

            assertThat(result).isNotNull();
            assertThat(result.getStatus()).isEqualTo(TransactionEntity.status.FAILED);

            // saveTransaction should NOT be called
            verify(transactionService, times(0)).saveTransaction(any(), any(), anyDouble(), any(), any(), any());
            verify(logService, times(1))
                    .logintoDB(userId, LogEntity.Action.TRANSACTIONS, "Deposit Failed", String.valueOf(userId), LogEntity.Status.FAILURE);
        }

        @Test
        void depositHandler_transactionServiceThrowsException_logsFailure() {
            when(transactionService.depositAmount(accountNumber, amount)).thenThrow(new RuntimeException("Service error"));

            assertThrows(RuntimeException.class, () -> depositAndWithdrawalOrch.depositHandler(userId, accountNumber, amount));

            // No logs or saveTransaction calls because exception thrown
            verify(transactionService, times(1)).depositAmount(accountNumber, amount);
            verify(transactionService, times(0)).saveTransaction(any(), any(), anyDouble(), any(), any(), any());
            verify(logService, times(0)).logintoDB(any(), any(), any(), any(), any());
        }

        @Test
        void depositHandler_nullAccount_throwsException() {
            assertThrows(IllegalArgumentException.class, () ->
                    depositAndWithdrawalOrch.depositHandler(userId, null, amount)
            );
        }


        // Withdraw Handler Tests
        @Test
        void withdrawalHandler_success() {
                DepositWithdrawDTO withdrawalSuccess = new DepositWithdrawDTO(accountNumber, "Success", amount, TransactionEntity.status.COMPLETED, TransactionEntity.type.WITHDRAWAL);
                when(transactionService.withdrawAmount(accountNumber, amount)).thenReturn(withdrawalSuccess);

                DepositWithdrawDTO result = depositAndWithdrawalOrch.WithdrawalHandler(userId, accountNumber, amount);

                assertThat(result).isNotNull();
                assertThat(result.getStatus()).isEqualTo(TransactionEntity.status.COMPLETED);

                verify(transactionService, times(1))
                        .saveTransaction(accountNumber, null, amount, "Withdrawal Transaction save Successful :)", TransactionEntity.type.WITHDRAWAL, TransactionEntity.status.COMPLETED);
                verify(logService, times(1))
                        .logintoDB(userId, LogEntity.Action.TRANSACTIONS, "Withdrawal Successful", String.valueOf(userId), LogEntity.Status.SUCCESS);
            }


        @Test
        void withdrawalHandler_nullUserId_throwsException() {
            assertThrows(IllegalArgumentException.class, () ->
                    depositAndWithdrawalOrch.WithdrawalHandler(null, accountNumber, amount));
        }


        @Test
        void withdrawalHandler_failedTransaction_returnsFailedDTOAndLogsFailure() {
            DepositWithdrawDTO withdrawalFailed = new DepositWithdrawDTO(accountNumber, "Failed", amount, TransactionEntity.status.FAILED, TransactionEntity.type.WITHDRAWAL);
            when(transactionService.withdrawAmount(accountNumber, amount)).thenReturn(withdrawalFailed);

            DepositWithdrawDTO result = depositAndWithdrawalOrch.WithdrawalHandler(userId, accountNumber, amount);

            assertThat(result).isNotNull();
            assertThat(result.getStatus()).isEqualTo(TransactionEntity.status.FAILED);

            verify(transactionService, times(0)).saveTransaction(any(), any(), anyDouble(), any(), any(), any());
            verify(logService, times(1))
                    .logintoDB(userId, LogEntity.Action.TRANSACTIONS, "Withdrawal Failed", String.valueOf(userId), LogEntity.Status.FAILURE);
        }


        @Test
        void withdrawalHandler_transactionServiceThrowsException_logsFailure() {
            when(transactionService.withdrawAmount(accountNumber, amount)).thenThrow(new RuntimeException("Service error"));

            assertThrows(RuntimeException.class, () -> depositAndWithdrawalOrch.WithdrawalHandler(userId, accountNumber, amount));

            verify(transactionService, times(1)).withdrawAmount(accountNumber, amount);
            verify(transactionService, times(0)).saveTransaction(any(), any(), anyDouble(), any(), any(), any());
            verify(logService, times(0)).logintoDB(any(), any(), any(), any(), any());
        }
    }

