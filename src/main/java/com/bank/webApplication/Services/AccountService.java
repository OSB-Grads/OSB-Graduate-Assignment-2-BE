package com.bank.webApplication.Services;


import com.bank.webApplication.CustomException.AccessDeniedException;
import com.bank.webApplication.CustomException.AccountNotFoundException;
import com.bank.webApplication.CustomException.UserNotFoundException;
import com.bank.webApplication.Dto.AccountDto;
import com.bank.webApplication.Entity.AccountEntity;
import com.bank.webApplication.Entity.LogEntity;
import com.bank.webApplication.Entity.ProductEntity;
import com.bank.webApplication.Entity.UserEntity;
import com.bank.webApplication.Repository.AccountRepository;
import com.bank.webApplication.Repository.ProductRepository;
import com.bank.webApplication.Repository.UserRepository;
import com.bank.webApplication.Util.DtoEntityMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;


@Slf4j
@Service
public class AccountService {
    @Autowired
    private DtoEntityMapper dtoEntityMapper;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private LogService logService;

    public AccountDto CreateAccount(AccountDto accountDto,String userId,String productId ) throws SQLException {
        log.info("[AccountService]  CreateAccount entered SUCCESS");
        UUID id=UUID.fromString(userId);
        UserEntity user=userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("user not found! create the profile before create the account"));
        ProductEntity product=productRepository.findByProductId(productId);

        boolean created = false;
        int attempts = 0;


        LocalDateTime now = LocalDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = now.format(formatter);

        accountDto.setAccountCreated(formattedDate);
        accountDto.setAccountUpdated(formattedDate);


        while (!created && attempts < 5) {
            attempts++;
            String accountNumber = generateUniqueAccountNumberUUID();
            accountDto.setAccountNumber(accountNumber);

          AccountEntity accountEntity = dtoEntityMapper.convertToEntity(accountDto,AccountEntity.class);
          accountEntity.setUser(user);
          accountEntity.setProduct(product);

            try {


                accountRepository.save(accountEntity);
                log.info("[Account Handler] Account Creation Operation Successful :) ");
                logService.logintoDB(id, LogEntity.Action.TRANSACTIONS, "Account Created  Successful", accountDto.getAccountCreated(), LogEntity.Status.SUCCESS);
                log.info("[Account Handler]  Saved Successfully in Logs");

                created = true;
                System.out.println("Account created successfully! Account Number: " + accountNumber);

            } catch (RuntimeException e) {
                if (e.getMessage().contains("UNIQUE") || e.getMessage().contains("constraint")) {
                    System.out.println("Duplicate account number detected, regenerating... (Attempt " + attempts + ")");
                } else {
                    throw e;
                }
            }
        }

        return accountDto;

    }
    private String generateUniqueAccountNumberUUID() {
        return String.valueOf(Math.abs(UUID.randomUUID().getMostSignificantBits())).substring(0, 10);
    }

    public AccountDto getOneAccount(String accountNumber){
        AccountEntity accountEntity=accountRepository.findByAccountNumber(accountNumber);
        double accountInterest =accountEntity.getProduct().getInterestRate();
        if(accountEntity==null){
            throw new AccountNotFoundException("Account not exit");
        }
        UUID id= accountEntity.getUser().getId();
        String curentUser=SecurityContextHolder.getContext().getAuthentication().getName();

        if(id==UUID.fromString(curentUser)){
            throw new AccessDeniedException("You are not authorized to access this account");
        }

        AccountDto dto=dtoEntityMapper.convertToDto(accountEntity,AccountDto.class);
        log.info("[Account Handler] Account Information Displayed Successfully :) ");
        logService.logintoDB(id, LogEntity.Action.TRANSACTIONS, "Account Created  Successful",accountNumber, LogEntity.Status.SUCCESS);
        log.info("[Account Handler]  Saved Successfully in Logs");

        return dto;

    }

    public List<AccountDto> getAllAccountsByUserId(String  userId){
        UUID id=UUID.fromString(userId);

        List<AccountEntity>accounts=accountRepository.findAllByUserId(id);


        List<AccountDto>accountDtos=accounts.stream()
                .map(account->dtoEntityMapper.convertToDto(account,AccountDto.class))
                .collect(Collectors.toList());

        log.info(" Accounts Information Displayed Successfully ");
        logService.logintoDB(id, LogEntity.Action.TRANSACTIONS, "Account retrieval  Successful", "ALl Counts", LogEntity.Status.SUCCESS);
        log.info("[Account Handler]  Saved Successfully in Logs");


        return  accountDtos;

    }

}

