package com.bank.demo.services;

import com.bank.demo.dto.BalanceDto;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountService {
    private static final String BASE_URL = "https://sandbox.platfr.io";

    public Optional<BalanceDto> getBalance(Long accountId) {
        return null;
    }

    public String getTransactions(Long accountId) {
        return "";
    }

    public String commitTransfer(Long accountId) {
        return "";
    }
}
