package com.bank.webApplication.Dto;

import com.bank.webApplication.Entity.TransactionEntity;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDTO {
    private String transactionId;
    private String fromAccount;
    private String toAccount;
    private  String description;
    private double amount;
    private TransactionEntity.status status;
    private TransactionEntity.type type;
}
