package com.apexbank.txn.service;

import java.security.SecureRandom;

public class ReferenceIdGenerator {
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generate() {
        long timestampPart = System.currentTimeMillis() % 1_000_000_000L;
        int randomPart = RANDOM.nextInt(9000) + 1000;
        return "REF" + timestampPart + randomPart;
    }
}
