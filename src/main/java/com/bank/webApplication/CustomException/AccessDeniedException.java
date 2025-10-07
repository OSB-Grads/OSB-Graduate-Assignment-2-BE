package com.bank.webApplication.CustomException;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String msg){
        super(msg);
    }
}
