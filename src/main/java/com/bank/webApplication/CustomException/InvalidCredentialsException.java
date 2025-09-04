package com.bank.webApplication.CustomException;

import org.springframework.stereotype.Component;


public class InvalidCredentialsException extends RuntimeException{
    public InvalidCredentialsException(String msg){
        super(msg);
    }
}
