package com.bank.webApplication.Dto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepositWithdrawRequestDTO {
    private String accountNumber;
    private double amount;
}
