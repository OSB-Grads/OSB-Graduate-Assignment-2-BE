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

    private int  productId;
    private double interestRate;
    private int fixedWindow;
    private String description;
}
