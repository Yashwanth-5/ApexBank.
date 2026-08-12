package com.apexbank.txn.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayeeResponse {
    private Long id;
    private String payeeName;
    private String payeeAccountNumber;
    private String nickname;
}
