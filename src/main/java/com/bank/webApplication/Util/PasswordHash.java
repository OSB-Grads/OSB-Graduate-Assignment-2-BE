package com.bank.webApplication.Util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
@Slf4j
@RequiredArgsConstructor
@Service
public class PasswordHash {
    
    static BCryptPasswordEncoder encode=new BCryptPasswordEncoder();
    //method to hash password
    public static String HashPass(String RawPassword){
        log.info("[PasswordHash] HashPass entered  SUCCESS");
        String hashedPassword=encode.encode(RawPassword);
        log.info("[PasswordHash] HashPass   SUCCESS");
        return hashedPassword;
    }

}
