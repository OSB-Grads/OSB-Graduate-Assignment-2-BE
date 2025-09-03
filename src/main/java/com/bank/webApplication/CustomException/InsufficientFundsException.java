package com.bank.webApplication.CustomException;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.stereotype.Component;



public class InsufficientFundsException extends RuntimeException{
    public InsufficientFundsException(String msg){
        super(msg);
    }
}
