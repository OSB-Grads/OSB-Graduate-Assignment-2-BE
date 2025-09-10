package com.bank.webApplication.Services;

import com.bank.webApplication.CustomException.AccountNotFoundException;
import com.bank.webApplication.CustomException.InsufficientFundsException;
import com.bank.webApplication.CustomException.TransactionFailedException;
import com.bank.webApplication.Dto.DepositWithdrawDTO;
import com.bank.webApplication.Dto.TransactionDTO;
import com.bank.webApplication.Entity.AccountEntity;
import com.bank.webApplication.Entity.ProductEntity;
import com.bank.webApplication.Entity.TransactionEntity;
import com.bank.webApplication.Repository.AccountRepository;
import com.bank.webApplication.Repository.TransactionRepository;
import com.bank.webApplication.Util.DtoEntityMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private DtoEntityMapper dtoEntityMapper;

    @InjectMocks
    private TransactionService transactionService;

    private AccountEntity account;
    private ProductEntity product;

    @BeforeEach
    void setUp() {
        product = new ProductEntity();
        product.setTenure(12);
        product.setFundingWindow(3);
        product.setCoolingPeriod(2);

        account = new AccountEntity();
        account.setAccountNumber("ACC123");
        account.setBalance(1000.0);
        account.setProduct(product);
        account.setAccountCreated(LocalDateTime.now().minusMonths(1).toString());
    }

    // DepositAmount Test Cases

    @Test
    void depositAmount_successful() {
        // Mock
        when(accountRepository.findById("ACC123")).thenReturn(Optional.of(account));
        when(accountRepository.save(any(AccountEntity.class))).thenReturn(account);

        DepositWithdrawDTO result = transactionService.depositAmount("ACC123", 5000);

        assertNotNull(result);
        assertThat(result.getStatus()).isEqualTo(TransactionEntity.status.COMPLETED);
        assertThat(result.getType()).isEqualTo(TransactionEntity.type.DEPOSIT);
        assertThat(account.getBalance()).isEqualTo(6000.0);
    }

    @Test
    void depositAmount_AmountLessThanZero() {
        InsufficientFundsException exception = assertThrows(InsufficientFundsException.class, () -> {
            transactionService.depositAmount("ACC123", -10); // Deposit a negative amount
        });

        assertThat(exception.getMessage()).contains("Deposit amount must be positive");
        verifyNoInteractions(accountRepository);
    }

    @Test
    void depositAmount_lockedAccount_returnsFailedDTO() {
        account.setAccountCreated(LocalDateTime.now().minusHours(7).toString()); // locked period
        when(accountRepository.findById("ACC123")).thenReturn(Optional.of(account));

        DepositWithdrawDTO dto = transactionService.depositAmount("ACC123", 500);
        assertThat(dto.getStatus()).isEqualTo(TransactionEntity.status.FAILED);
        assertThat(dto.getDescription()).isEqualTo("Account is currently locked due to the funding window.");
    }

    @Test
    void depositAmount_saveFails_returnsFailedDTO() {
        when(accountRepository.findById("ACC123")).thenReturn(Optional.of(account));
        when(accountRepository.save(any(AccountEntity.class))).thenThrow(new RuntimeException("Database error during deposit"));

        DepositWithdrawDTO result = transactionService.depositAmount("ACC123", 500);

        assertThat(result.getStatus()).isEqualTo(TransactionEntity.status.FAILED);
        assertThat(result.getDescription()).contains("Database error during deposit");
    }


    // Withdraw Tests

    @Test
    void withdrawAmount_successful() {
        when(accountRepository.findById("ACC123")).thenReturn(Optional.of(account));
        when(accountRepository.save(any(AccountEntity.class))).thenReturn(account);

        DepositWithdrawDTO result = transactionService.withdrawAmount("ACC123", 500);

        assertNotNull(result);
        assertThat(result.getStatus()).isEqualTo(TransactionEntity.status.COMPLETED);
        assertThat(account.getBalance()).isEqualTo(500.0);
    }

    @Test
    void withdrawAmount_amountLessThanOrEqualZero() {
        InsufficientFundsException exception = assertThrows(InsufficientFundsException.class, () -> {
            transactionService.withdrawAmount("ACC123", -100); // Withdraw a negative amount
        });

        assertThat(exception.getMessage()).contains("Amount should be greater than 0");

        verifyNoInteractions(accountRepository);
    }

    @Test
    void withdrawAmount_insufficientFunds() {
        when(accountRepository.findById("ACC123")).thenReturn(Optional.of(account));

        assertThatThrownBy(() -> transactionService.withdrawAmount("ACC123", 2000))
                .isInstanceOf(InsufficientFundsException.class)
                .hasMessageContaining("Insufficient funds available for this withdrawal");
    }

    @Test
    void withdrawAmount_lockedAccount_returnsFailedDTO() {
        account.setAccountCreated(LocalDateTime.now().minusHours(7).toString()); // locked period
        when(accountRepository.findById("ACC123")).thenReturn(Optional.of(account));

        DepositWithdrawDTO dto = transactionService.withdrawAmount("ACC123", 500);
        assertThat(dto.getStatus()).isEqualTo(TransactionEntity.status.FAILED);
        assertThat(dto.getDescription()).isEqualTo("Account is currently locked due to the funding window.");
    }

    @Test
    void withdrawAmount_saveFails_returnsFailedDTO() {
        when(accountRepository.findById("ACC123")).thenReturn(Optional.of(account));
        when(accountRepository.save(any(AccountEntity.class))).thenThrow(new RuntimeException("Database error during withdrawal"));

        DepositWithdrawDTO result = transactionService.withdrawAmount("ACC123", 100);

        assertThat(result.getStatus()).isEqualTo(TransactionEntity.status.FAILED);
        assertThat(result.getDescription()).contains("Database error during withdrawal");
    }


    // isLocked Tests

    @Test
    void isLocked_duringFunding_returnsFalse() {
        account.setAccountCreated(LocalDateTime.now().minusHours(1).toString()); // within funding
        boolean locked = transactionService.isLocked(account);
        assertThat(locked).isFalse();
    }

    @Test
    void isLocked_duringMaturity_returnsTrue() {
        account.setAccountCreated(LocalDateTime.now().minusHours(7).toString()); // after funding, before cooling
        boolean locked = transactionService.isLocked(account);
        assertThat(locked).isTrue();
    }

    @Test
    void isLocked_duringCooling_returnsFalse() {
        account.setAccountCreated(LocalDateTime.now().minusHours(23).toString()); // in cooling
        boolean locked = transactionService.isLocked(account);
        assertThat(locked).isFalse();
    }

    @Test
    void isLocked_afterTenure_returnsFalse() {
        account.setAccountCreated(LocalDateTime.now().minusHours(25).toString()); // after tenure
        boolean locked = transactionService.isLocked(account);
        assertThat(locked).isFalse();
    }


    // SaveTransaction Tests

    @Test
    void saveTransaction_successful() {
        AccountEntity acc = new AccountEntity();
        acc.setAccountNumber("ACC123");
        when(accountRepository.findById("ACC123")).thenReturn(Optional.of(acc));
        when(accountRepository.findById("ACC456")).thenReturn(Optional.of(acc));

        transactionService.saveTransaction("ACC123", "ACC456", 100.0,
                "Test", TransactionEntity.type.DEPOSIT, TransactionEntity.status.COMPLETED);

        verify(transactionRepository, times(1)).save(any(TransactionEntity.class));
    }

    @Test
    void saveTransaction_failure_throwsTransactionFailedException() {
        AccountEntity acc = new AccountEntity();
        acc.setAccountNumber("ACC123");
        when(accountRepository.findById("ACC123")).thenReturn(Optional.of(acc));
        when(accountRepository.findById("ACC456")).thenReturn(Optional.of(acc));
        doThrow(new RuntimeException("Database error during transaction save")).when(transactionRepository).save(any(TransactionEntity.class));

        assertThatThrownBy(() -> transactionService.saveTransaction("ACC123", "ACC456", 100.0,
                "Test", TransactionEntity.type.DEPOSIT, TransactionEntity.status.COMPLETED))
                .isInstanceOf(TransactionFailedException.class)
                .hasMessageContaining("Transaction save failed due to a database error");
    }

    // GetTransactionsByAccountNumber Tests

    @Test
    void getTransactionsByAccountNumber_returnsCombinedList() {
        TransactionEntity t1 = new TransactionEntity();
        TransactionEntity t2 = new TransactionEntity();

        when(transactionRepository.findAllByFromAccountAccountNumber("ACC123"))
                .thenReturn(List.of(t1));
        when(transactionRepository.findAllByToAccountAccountNumber("ACC123"))
                .thenReturn(List.of(t2));
        when(dtoEntityMapper.convertToDto(any(), eq(TransactionDTO.class)))
                .thenReturn(new TransactionDTO());

        List<TransactionDTO> result = transactionService.getTransactionsByAccountNumber("ACC123");

        assertThat(result).hasSize(2);
    }
}
