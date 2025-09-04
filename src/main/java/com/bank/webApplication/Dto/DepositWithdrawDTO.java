package com.bank.webApplication.Dto;
import com.bank.webApplication.Entity.TransactionEntity;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepositWithdrawDTO {
    private String Accountnumber;
    private  String description;
    private double amount;
    private TransactionEntity.status status;
    private TransactionEntity.type type;
}
