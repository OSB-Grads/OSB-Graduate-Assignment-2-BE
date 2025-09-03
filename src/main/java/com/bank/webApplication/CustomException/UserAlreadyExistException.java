package com.bank.webApplication.CustomException;

import org.springframework.stereotype.Component;


public class UserAlreadyExistException extends RuntimeException{
    public UserAlreadyExistException(String msg){super(msg);}
}
