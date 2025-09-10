package com.bank.webApplication.CustomException;

public class LogNotFoundException extends RuntimeException{
    public LogNotFoundException(String msg){
        super(msg);
    }
}
