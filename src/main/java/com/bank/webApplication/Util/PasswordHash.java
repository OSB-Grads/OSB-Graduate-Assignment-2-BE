package com.bank.webApplication.Util;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
@RequiredArgsConstructor
@Service
public class PasswordHash {
    
    private static final PasswordEncoder password = null;
    //method to hash password
    public static String HashPass(String RawPassword){
        String hashedPassword=password.encode(RawPassword);
        return hashedPassword;
    }

}
