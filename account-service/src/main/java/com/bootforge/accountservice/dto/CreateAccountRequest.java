package com.bootforge.accountservice.dto;

import com.bootforge.accountservice.entity.AccountType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CreateAccountRequest(

        @NotBlank(message = "Account Holder Name is required")
        String accountHolderName,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Phone Number is required")
        String phone,

        @NotNull(message = "Account Type is required")
        AccountType accountType,

        @NotNull(message = "Initial Deposit is required")
        @Positive(message = "Initial Deposit must be positive")
        BigDecimal initialDeposit
        ) {
}
