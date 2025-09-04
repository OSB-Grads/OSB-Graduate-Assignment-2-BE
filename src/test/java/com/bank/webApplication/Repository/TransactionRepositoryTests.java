package com.bank.webApplication.Repository;

import com.bank.webApplication.Entity.AccountEntity;
import com.bank.webApplication.Entity.TransactionEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class )
public class TransactionRepositoryTests {

    @Mock
    private TransactionRepository transactionRepository;

    @Test
    void findAllByToAccountAccountNumberTest(){

        String toAccountNumber = "1234567890";

        TransactionEntity transactionEntity = new TransactionEntity();
        transactionEntity.setTransactionId(UUID.randomUUID());

        AccountEntity account = new AccountEntity();
        account.setAccountNumber(toAccountNumber);
        transactionEntity.setToAccount(account);

        List<TransactionEntity> mockTransactions = List.of(transactionEntity);

        when(transactionRepository.findAllByToAccountAccountNumber(toAccountNumber)).thenReturn(mockTransactions);

        List<TransactionEntity> result = transactionRepository.findAllByToAccountAccountNumber(toAccountNumber);

        assertThat(result).isNotEmpty();
        assertThat(result.getFirst().getToAccount().getAccountNumber()).isEqualTo(toAccountNumber);
    }

    @Test
    void findAllByFromAccountAccountNumberTest(){

        String fromAccountNumber = "1234567890";

        TransactionEntity transactionEntity = new TransactionEntity();
        transactionEntity.setTransactionId(UUID.randomUUID());

        AccountEntity account = new AccountEntity();
        account.setAccountNumber(fromAccountNumber);
        transactionEntity.setFromAccount(account);

        List<TransactionEntity> mockTransactions = List.of(transactionEntity);

        when(transactionRepository.findAllByFromAccountAccountNumber(fromAccountNumber)).thenReturn(mockTransactions);

        List<TransactionEntity> result = transactionRepository.findAllByFromAccountAccountNumber(fromAccountNumber);

        assertThat(result).isNotEmpty();
        assertThat(result.getFirst().getFromAccount().getAccountNumber()).isEqualTo(fromAccountNumber);
    }

    @Test
    void testFindAllByToAccountAccountNumber_ForNoTransactions() {
        String toAccountNumber = "12345678";

        when(transactionRepository.findAllByToAccountAccountNumber(toAccountNumber))
                .thenReturn(List.of());

        List<TransactionEntity> result = transactionRepository.findAllByToAccountAccountNumber(toAccountNumber);

        assertThat(result).isEmpty();
    }

    @Test
    void testFindAllByFromAccountAccountNumber_ForNoTransactions() {
        String fromAccountNumber = "12345678";

        when(transactionRepository.findAllByFromAccountAccountNumber(fromAccountNumber))
                .thenReturn(List.of());

        List<TransactionEntity> result = transactionRepository.findAllByFromAccountAccountNumber(fromAccountNumber);

        assertThat(result).isEmpty();
    }

}
