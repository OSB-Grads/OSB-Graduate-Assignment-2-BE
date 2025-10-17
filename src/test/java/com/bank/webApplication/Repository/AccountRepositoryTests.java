package com.bank.webApplication.Repository;

import com.bank.webApplication.Entity.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountRepositoryTests {

    //    AccountEntity findByAccountNumber(String s);
//    List<AccountEntity> findAllByUserId(UUID id);
    @Mock
    private AccountRepository accountRepository;
    private AccountEntity account1, account2;
    private String testAccountNumber1, testAccountNumber2;
    UUID userId;

    @BeforeEach
    public void createAccount() {
        testAccountNumber1 = UUID.randomUUID().toString();
        testAccountNumber2 = UUID.randomUUID().toString();

        UserEntity user = new UserEntity(
                userId = UUID.randomUUID(),
                "testUser",
                "test@email.com",
                "7894561233", "25/9/2025", "25/9/2025", "bengaluru", Role.USER);
        ProductEntity product1 = new ProductEntity("SAV01", "testProduct", 2.1, 0, 0, 0, "unitTest");
        ProductEntity product2 = new ProductEntity("FD01", "testProduct", 5.8, 2, 2, 5, "unitTest");

        account1 = new AccountEntity(testAccountNumber1, user, product1, AccountEntity.accountType.SAVINGS, 10000, "25/09/2025", "30/09/2025");
        account2 = new AccountEntity(testAccountNumber2, user, product2, AccountEntity.accountType.FIXED_DEPOSIT, 10000, "25/09/2025", "30/09/2025");

    }


    @Test
    void testFindByAccountNumber_Found() {
        when(accountRepository.findByAccountNumber(testAccountNumber1)).thenReturn(account1);
        when(accountRepository.findByAccountNumber(testAccountNumber2)).thenReturn(account2);

        AccountEntity resultAccount1 = accountRepository.findByAccountNumber(testAccountNumber1);
        AccountEntity resultAccount2 = accountRepository.findByAccountNumber((testAccountNumber2));

        assertThat(resultAccount1).isNotNull();
        assertThat(resultAccount1).isEqualTo(account1);
        assertThat((resultAccount1.getAccountNumber())).isEqualTo(testAccountNumber1);
        assertThat((resultAccount1.getAccountType())).isEqualTo(AccountEntity.accountType.SAVINGS);

        assertThat((resultAccount2)).isNotNull();
        assertThat((resultAccount2)).isEqualTo(account2);
        assertThat((resultAccount2.getAccountNumber())).isEqualTo(testAccountNumber2);
        assertThat((resultAccount2.getAccountType())).isEqualTo(AccountEntity.accountType.FIXED_DEPOSIT);
    }

    @Test
    void testForAllByUserId() {
        List<AccountEntity> mock = new ArrayList<>();
        mock.add(account1);
        mock.add(account2);

        when(accountRepository.findAllByUserId(userId)).thenReturn(mock);
        List<AccountEntity> result = accountRepository.findAllByUserId(userId);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(mock);

    }
}
