package com.bank.webApplication.Dto;

import com.bank.webApplication.Entity.AccountEntity;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor


public class AccountDto {
    private String accountNumber;
//    private UUID userId; // Only the user's UUID
    private AccountEntity.accountType accountType;
    private double balance;
    private String accountCreated;
    private String accountUpdated;






}
