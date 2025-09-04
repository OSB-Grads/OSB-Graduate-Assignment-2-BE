package com.bank.webApplication.Dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

    private String productId;
    private String productName;
    private double interestRate;
    private int fundingWindow;
    private int coolingPeriod;
    private int Tenure;
    private String description;
}
