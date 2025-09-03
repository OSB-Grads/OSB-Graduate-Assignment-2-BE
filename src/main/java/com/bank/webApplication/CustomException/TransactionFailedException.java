package com.bank.webApplication.CustomException;

public class TransactionFailedException extends RuntimeException{
    public TransactionFailedException(String msg){
        super(msg);
    }
}
