package com.bank.webApplication.CustomException;


import org.springframework.stereotype.Component;

public class InvalidAccountTypeException extends RuntimeException{
    public InvalidAccountTypeException(String msg){
        super(msg);
    }
}
