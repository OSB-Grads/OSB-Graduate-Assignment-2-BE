package com.bank.webApplication.Dto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionRequestDTO {
    private String fromAccountNumber;
    private String toAccountNumber;
    private double amount;
}
