package com.bank.webApplication.Util;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class SensitiveDataValidator {
    private static final Pattern API_KEY = Pattern.compile("[A-Za-z0-9_\\-]{16,32}");
    private static final Pattern PASSWORDS = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}$");
    private static final Pattern JWT = Pattern.compile("(?i)(Bearer\\s+[A-Za-z0-9\\-_]+\\.[A-Za-z0-9\\-_]+\\.[A-Za-z0-9\\-_]+)");

    public boolean containsSensitiveData(String input) {
        if (input == null) return false;

        return API_KEY.matcher(input).find() ||
                PASSWORDS.matcher(input).find() ||
                JWT.matcher(input).find();
    }
}
