package com.bank.webApplication.Util;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
@RequiredArgsConstructor
@Service
public class PasswordHash {
    
    private static final PasswordEncoder password = null;
    static BCryptPasswordEncoder encode=new BCryptPasswordEncoder();
    //method to hash password
    public static String HashPass(String RawPassword){
        String hashedPassword=encode.encode(RawPassword);
        return hashedPassword;
    }

}
