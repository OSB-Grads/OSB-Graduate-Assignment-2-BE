package com.bank.webApplication.CustomException;

import jakarta.persistence.criteria.CriteriaBuilder;

public class InsufficientFundsException extends RuntimeException{
    public InsufficientFundsException(String msg){
        super(msg);
    }
}
