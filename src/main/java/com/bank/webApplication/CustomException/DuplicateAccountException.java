package com.bank.webApplication.CustomException;

public class DuplicateAccountException extends RuntimeException{
    public  DuplicateAccountException(String msg){
        super(msg);
    }
}
