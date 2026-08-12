package com.apexbank.txn.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class StatementFilterRequest {
    @NotNull private LocalDate fromDate;
    @NotNull private LocalDate toDate;
}
