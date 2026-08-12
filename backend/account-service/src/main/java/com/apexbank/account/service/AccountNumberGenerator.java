package com.apexbank.account.service;

import java.security.SecureRandom;

public class AccountNumberGenerator {
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generate() {
        StringBuilder sb = new StringBuilder("1");
        for (int i = 0; i < 11; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }
}
