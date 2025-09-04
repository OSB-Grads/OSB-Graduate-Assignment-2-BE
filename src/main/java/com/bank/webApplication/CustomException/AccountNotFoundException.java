package com.bank.webApplication.CustomException;


import org.springframework.stereotype.Component;



public class AccountNotFoundException extends RuntimeException{
    public AccountNotFoundException(String msg) {
        super(msg);
    }
}
