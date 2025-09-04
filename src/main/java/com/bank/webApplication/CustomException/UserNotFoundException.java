package com.bank.webApplication.CustomException;


import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;


public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String msg){
        super(msg);
    }
}
