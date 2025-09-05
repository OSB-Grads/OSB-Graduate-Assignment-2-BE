package com.bank.webApplication.Orchestrator;

import com.bank.webApplication.Dto.DepositWithdrawDTO;
import com.bank.webApplication.Dto.TransactionDTO;
import com.bank.webApplication.Entity.LogEntity;
import com.bank.webApplication.Entity.TransactionEntity;
import com.bank.webApplication.Services.LogService;
import com.bank.webApplication.Services.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class TransactOrchestratorTests {
    @Mock
    private TransactionService transactionService;
    @Mock
    private LogService logService;

    @InjectMocks
    private TransactOrchestrator orchestrator;

    private UUID userId;
    private String fromAccount;
    private String toAccount;
    private double amount;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userId = UUID.randomUUID();
        fromAccount = "ACC123";
        toAccount = "ACC456";
        amount = 500.0;
    }
    @Test
    void transactionBetweenAccounts_sameAccounts_returnsFailedDTO() {
        TransactionDTO result = orchestrator.transactionBetweenAccounts(userId, fromAccount, fromAccount, amount);

        assertThat(result.getStatus()).isEqualTo(TransactionEntity.status.FAILED);
        assertThat(result.getDescription()).isEqualTo("Cannot transfer to the same account");

        verify(transactionService, never()).withdrawAmount(anyString(), anyDouble());
        verify(transactionService, never()).depositAmount(anyString(), anyDouble());
        verify(logService).logintoDB(userId, LogEntity.Action.TRANSACTIONS,
                "Transaction Failed", String.valueOf(userId), LogEntity.Status.FAILURE);
    }

    @Test
    void transactionBetweenAccounts_successfulTransaction_returnsCompletedDTO() {
        // Mock behaviour
        DepositWithdrawDTO withdrawDTO = new DepositWithdrawDTO();
        DepositWithdrawDTO depositDTO = new DepositWithdrawDTO();

        when(transactionService.withdrawAmount(fromAccount, amount)).thenReturn(withdrawDTO);
        when(transactionService.depositAmount(toAccount, amount)).thenReturn(depositDTO);

        // Logic
        TransactionDTO result = orchestrator.transactionBetweenAccounts(userId, fromAccount, toAccount, amount);

       // Assertions
        assertThat(result).isNotNull();
        assertThat(result.getFromAccount()).isEqualTo(fromAccount);
        assertThat(result.getToAccount()).isEqualTo(toAccount);
        assertThat(result.getAmount()).isEqualTo(amount);
        assertThat(result.getStatus()).isEqualTo(TransactionEntity.status.COMPLETED);
        assertThat(result.getType()).isEqualTo(TransactionEntity.type.TRANSFER);
        assertThat(result.getDescription()).isEqualTo("Transaction Successful");

        // Verify service calls
        verify(transactionService).withdrawAmount(fromAccount, amount);
        verify(transactionService).depositAmount(toAccount, amount);
        verify(transactionService).saveTransaction(fromAccount, toAccount, amount,
                    "Transaction Saved Successfully", TransactionEntity.type.TRANSFER, TransactionEntity.status.COMPLETED);

        verify(logService).logintoDB(userId, LogEntity.Action.TRANSACTIONS,
                    "Transaction is Successful", String.valueOf(userId), LogEntity.Status.SUCCESS);
    }

        @Test
        void transactionBetweenAccounts_withdrawFails_returnsFailedDTO() {
            // Arrange
            when(transactionService.withdrawAmount(fromAccount, amount)).thenReturn(null);

            // Act
            TransactionDTO result = orchestrator.transactionBetweenAccounts(userId, fromAccount, toAccount, amount);

            // Assert
            assertThat(result.getStatus()).isEqualTo(TransactionEntity.status.FAILED);
            assertThat(result.getDescription()).isEqualTo("Transaction Failed at Withdrawal Level");

            // Verify deposit never called
            verify(transactionService, never()).depositAmount(anyString(), anyDouble());
            verify(logService).logintoDB(userId, LogEntity.Action.TRANSACTIONS,
                    "Transaction Failed", String.valueOf(userId), LogEntity.Status.FAILURE);
        }

        @Test
        void transactionBetweenAccounts_depositFails_returnsFailedDTO() {
            // Mock
            DepositWithdrawDTO withdrawDTO = new DepositWithdrawDTO();
            when(transactionService.withdrawAmount(fromAccount, amount)).thenReturn(withdrawDTO);
            when(transactionService.depositAmount(toAccount, amount)).thenReturn(null);

            // Act
            TransactionDTO result = orchestrator.transactionBetweenAccounts(userId, fromAccount, toAccount, amount);

            // Assertions
            assertThat(result.getStatus()).isEqualTo(TransactionEntity.status.FAILED);
            assertThat(result.getDescription()).isEqualTo("Transaction Failed at Deposit Level.");
            assertThat(result.getFromAccount()).isEqualTo(fromAccount);
            assertThat(result.getToAccount()).isEqualTo(toAccount);

            // Verify services
            verify(transactionService).withdrawAmount(fromAccount, amount);
            verify(transactionService).depositAmount(toAccount, amount);
            verify(transactionService, never()).saveTransaction(eq(fromAccount), eq(toAccount), anyDouble(),
                    anyString(), any(), any());

            verify(logService).logintoDB(userId, LogEntity.Action.TRANSACTIONS,
                    "Transaction Failed", String.valueOf(userId), LogEntity.Status.FAILURE);
        }
    }


