package com.bank.webApplication.CustomException;

public class InvalidAccountTypeException extends RuntimeException{
    public InvalidAccountTypeException(String msg){
        super(msg);
    }
}
