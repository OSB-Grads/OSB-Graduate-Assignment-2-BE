package com.bank.webApplication.Util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Random;
@Slf4j
@Component
public class OTPGenerator {
    public int generateOtp() {
        Random random = new Random();
        log.info("[OTP Generation] OTP Generation SUCCESS");
        return random.nextInt(100000, 999_999);

    }
}
