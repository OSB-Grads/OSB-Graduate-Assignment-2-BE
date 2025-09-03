package com.bank.webApplication.CustomException;

import org.springframework.stereotype.Component;


public class TransactionFailedException extends RuntimeException{
    public TransactionFailedException(String msg){
        super(msg);
    }
}
