//package com.bank.webApplication.Repository;
//
//
//import com.bank.webApplication.Entity.AccountEntity;
//import com.bank.webApplication.Entity.ProductEntity;
//import com.bank.webApplication.Entity.Role;
//import com.bank.webApplication.Entity.UserEntity;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//public class AccountRepositoryTests {
//
//    @Mock
//    private AccountRepository accountRepository;
//
//
//    String testAccountNumber="75694413356";
//    UserEntity userEntity=new UserEntity(
//            UUID.randomUUID(),
//            "Test123",
//            "test@email.com",
//            "7894561233","25/9/2025","25/9/2025","bengaluru", Role.USER);
//    ProductEntity productEntity=new ProductEntity("FD01","Test",5.8,2,2,5,"Test");
//    AccountEntity accountEntity=new AccountEntity(testAccountNumber,userEntity,productEntity, AccountEntity.accountType.SAVINGS,10000,"25/09/2025","30/09/2025");
//
//
//    @Test
//    void testForFindByAccountNumber(){
//        when(accountRepository.findByAccountNumber(testAccountNumber)).thenReturn(accountEntity);
//
//        AccountEntity result=accountRepository.findByAccountNumber(testAccountNumber);
//
//        assertThat(result).isNotNull();
//        assertThat(result).isEqualTo(accountEntity);
//    }
//
//    @Test
//    void testForAllByUserId(){
//        UUID testUUID=UUID.randomUUID();
//        List<AccountEntity> mock=new ArrayList<>();
//        mock.add(accountEntity);
//        when(accountRepository.findAllByUserId(testUUID)).thenReturn(mock);
//
//        List<AccountEntity> result=accountRepository.findAllByUserId(testUUID);
//
//        assertThat(result).isNotNull();
//        assertThat(result).isEqualTo(mock);
//
//
//    }
//}
