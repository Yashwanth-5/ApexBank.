package com.apexbank.txn.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddPayeeRequest {
    @NotBlank private String payeeName;
    @NotBlank private String payeeAccountNumber;
    @NotBlank private String confirmPayeeAccountNumber;
    private String nickname;
}
