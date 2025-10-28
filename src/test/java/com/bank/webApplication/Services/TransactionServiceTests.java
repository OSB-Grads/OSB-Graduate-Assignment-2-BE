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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTests {

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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        product = new ProductEntity();
        product.setTenure(12);
        product.setFundingWindow(3);
        product.setCoolingPeriod(2);

        account = new AccountEntity();
        account.setAccountNumber("ACC123");
        account.setBalance(1000.0);
        account.setProduct(product);
        account.setAccountCreated(LocalDateTime.now().minusMonths(1).format(formatter).toString());
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

        assertThat(exception.getMessage()).contains("Amount should be greater than 0");
        verifyNoInteractions(accountRepository);
    }

    @Test
    void depositAmount_lockedAccount_returnsFailedDTO() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        account.setAccountCreated(LocalDateTime.now().minusHours(7).format(formatter).toString()); // locked period
        when(accountRepository.findById("ACC123")).thenReturn(Optional.of(account));

        DepositWithdrawDTO dto = transactionService.depositAmount("ACC123", 500);
        assertThat(dto.getStatus()).isEqualTo(TransactionEntity.status.FAILED);
        assertThat(dto.getDescription()).isEqualTo("Account is Locked");
    }

    @Test
    void depositAmount_saveFails_returnsFailedDTO() {
        when(accountRepository.findById("ACC123")).thenReturn(Optional.of(account));
        when(accountRepository.save(any(AccountEntity.class))).thenThrow(new RuntimeException("Database error during deposit"));

        DepositWithdrawDTO result = transactionService.depositAmount("ACC123", 500);

        assertThat(result.getStatus()).isEqualTo(TransactionEntity.status.FAILED);
        assertThat(result.getDescription()).contains("Deposit Failed");
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
                .hasMessageContaining("1000.0 is Insufficient to debit.");
    }

    @Test
    void withdrawAmount_lockedAccount_returnsFailedDTO() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        account.setAccountCreated(LocalDateTime.now().minusHours(7).format(formatter).toString()); // locked period
        when(accountRepository.findById("ACC123")).thenReturn(Optional.of(account));

        DepositWithdrawDTO dto = transactionService.withdrawAmount("ACC123", 500);
        assertThat(dto.getStatus()).isEqualTo(TransactionEntity.status.FAILED);
        assertThat(dto.getDescription()).isEqualTo("Account is Locked");
    }

    @Test
    void withdrawAmount_saveFails_returnsFailedDTO() {
        when(accountRepository.findById("ACC123")).thenReturn(Optional.of(account));
        when(accountRepository.save(any(AccountEntity.class))).thenThrow(new RuntimeException("Database error during withdrawal"));

        DepositWithdrawDTO result = transactionService.withdrawAmount("ACC123", 100);

        assertThat(result.getStatus()).isEqualTo(TransactionEntity.status.FAILED);
        assertThat(result.getDescription()).contains("Withdrawal Failed");
    }


    // isLocked Tests

    @Test
    void isLocked_duringFunding_returnsFalse() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        account.setAccountCreated(LocalDateTime.now().minusHours(1).format(formatter).toString()); // within funding
        boolean locked = transactionService.isLocked(account);
        assertThat(locked).isFalse();
    }

    @Test
    void isLocked_duringMaturity_returnsTrue() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        account.setAccountCreated(LocalDateTime.now().minusHours(7).format(formatter).toString()); // after funding, before cooling
        boolean locked = transactionService.isLocked(account);
        assertThat(locked).isTrue();
    }

    @Test
    void isLocked_duringCooling_returnsFalse() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        account.setAccountCreated(LocalDateTime.now().minusHours(23).format(formatter).toString()); // in cooling
        boolean locked = transactionService.isLocked(account);
        assertThat(locked).isFalse();
    }

    @Test
    void isLocked_afterTenure_returnsFalse() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        account.setAccountCreated(LocalDateTime.now().minusHours(25).format(formatter).toString()); // after tenure
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
                .hasMessageContaining("Save Transaction Failed");
    }

    // GetTransactionsByAccountNumber Tests

    @Test
    void getTransactionsByAccountNumber_returnsCombinedList() {
        TransactionEntity t1 = new TransactionEntity(UUID.randomUUID(),account,null,1000,"Deposit",LocalDateTime.now().toString(), TransactionEntity.type.DEPOSIT, TransactionEntity.status.COMPLETED);
        TransactionEntity t2 = new TransactionEntity(UUID.randomUUID(),null,account,1000,"WithDrawal",LocalDateTime.now().toString(), TransactionEntity.type.WITHDRAWAL, TransactionEntity.status.COMPLETED);

        when(transactionRepository.findAllByFromAccountAccountNumber("ACC123"))
                .thenReturn(List.of(t1));
        when(transactionRepository.findAllByToAccountAccountNumber("ACC123"))
                .thenReturn(List.of(t2));

        List<TransactionDTO> result = transactionService.getTransactionsByAccountNumber("ACC123");

        assertThat(result).hasSize(2);
    }
}
