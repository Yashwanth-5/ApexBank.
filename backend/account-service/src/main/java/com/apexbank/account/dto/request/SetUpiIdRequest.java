package com.apexbank.account.dto.request;

import lombok.Data;

@Data
public class SetUpiIdRequest {
    private String customPrefix; // optional; defaults to account's mobile number if blank
}
