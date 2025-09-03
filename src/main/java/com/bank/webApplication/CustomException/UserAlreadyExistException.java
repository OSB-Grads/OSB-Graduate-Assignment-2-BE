package com.bank.webApplication.CustomException;
public class UserAlreadyExistException extends RuntimeException{
    public UserAlreadyExistException(String msg){super(msg);}
}
