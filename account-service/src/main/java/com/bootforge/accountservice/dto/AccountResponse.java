package com.bootforge.accountservice.dto;

import com.bootforge.accountservice.entity.AccountStatus;
import com.bootforge.accountservice.entity.AccountType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record AccountResponse(
        String id,
        String accountNumber,
        String accountHolderName,
        String email,
        String phone,
        AccountType accountType,
        AccountStatus accountStatus,
        BigDecimal balance,
        BigDecimal dailyTransactionLimit,
        LocalDateTime createdAt
) {
}
