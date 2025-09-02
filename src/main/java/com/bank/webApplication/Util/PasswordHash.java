package com.bank.webApplication.Util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
@RequiredArgsConstructor
@Service
public class PasswordHash {
    private final PasswordEncoder password;

    public static String HashPass(String RawPassword){
        String hashedPassword=password.encode(RawPassword);
        return hashedPassword;
    }

}
