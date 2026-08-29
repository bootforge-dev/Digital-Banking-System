package com.bootforge.accountservice.service;

import com.bootforge.accountservice.dto.AccountResponse;
import com.bootforge.accountservice.dto.CreateAccountRequest;
import com.bootforge.accountservice.entity.Account;
import com.bootforge.accountservice.entity.AccountStatus;
import com.bootforge.accountservice.entity.AccountType;
import com.bootforge.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;
    private static final SecureRandom secureRandom = new SecureRandom();


    public AccountResponse createAccount(CreateAccountRequest request) {
        log.info("Creating account for {}", request.email());

        if (accountRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Account already exists for email: " + request.email());
        }

        Account account = new Account();
        account.setAccountHolderName(request.accountHolderName());
        account.setEmail(request.email());
        account.setPhone(request.phone());
        account.setAccountType(request.accountType());
        account.setAccountStatus(AccountStatus.ACTIVE);
        account.setBalance(request.initialDeposit());
        account.setAccountNumber(generateAccountNumber());
        account.setDailyTransactionLimit(
                request.accountType() == AccountType.SAVINGS
                        ? new BigDecimal("100000")
                        : new BigDecimal("500000")
        );

        Account savedAccount = accountRepository.save(account);
        log.info("Account created: {}", savedAccount.getAccountNumber());

        return mapToResponse(savedAccount);
    }

    public AccountResponse getAccount(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        return mapToResponse(account);
    }

    public BigDecimal getBalance(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        return account.getBalance();
    }

    public void blockAccount(String accountNumber) {
        log.info("Blocking account: {}", accountNumber);

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        account.setAccountStatus(AccountStatus.BLOCKED);
        accountRepository.save(account);

        log.info("Account Blocked: {}", accountNumber);
    }

    public void deductBalance(String accountNumber, BigDecimal amount) {
        log.info("Deducting Balance {} from account: {}", amount, accountNumber);

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (account.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new RuntimeException("Account is not active: " + accountNumber);
        }

        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient funds to account: " + accountNumber);
        }

        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);

        log.info("Balance updated. New balance: {}", account.getBalance());
    }

    public void creditBalance(String accountNumber, BigDecimal amount) {
        log.info("Crediting {} to account: {}", amount, accountNumber);

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);

        log.info("Balance Credited. New balance: {}", account.getBalance());
    }

    private String generateAccountNumber() {
        String accountNumber;
        do {

            long number = secureRandom.nextLong(1_000_000_000_000L);
            accountNumber = String.format("%012d", number);

        } while (accountRepository.existsByAccountNumber(accountNumber));

        return accountNumber;
    }

    private AccountResponse mapToResponse(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .accountHolderName(account.getAccountHolderName())
                .email(account.getEmail())
                .phone(account.getPhone())
                .accountType(account.getAccountType())
                .accountStatus(account.getAccountStatus())
                .balance(account.getBalance())
                .dailyTransactionLimit(account.getDailyTransactionLimit())
                .createdAt(account.getCreatedAt())
                .build();
    }
}
