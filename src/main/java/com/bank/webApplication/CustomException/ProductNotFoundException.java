package com.bank.webApplication.CustomException;

public class ProductNotFoundException extends RuntimeException{
    public ProductNotFoundException(String msg){super(msg);}
}
