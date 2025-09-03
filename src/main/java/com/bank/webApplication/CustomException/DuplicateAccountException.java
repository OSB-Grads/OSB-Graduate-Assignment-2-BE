package com.bank.webApplication.CustomException;


import org.springframework.stereotype.Component;


public class DuplicateAccountException extends RuntimeException{
    public  DuplicateAccountException(String msg){
        super(msg);
    }
}
