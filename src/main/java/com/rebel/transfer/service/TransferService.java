package com.rebel.transfer.service;

import com.rebel.transfer.model.Account;
import com.rebel.transfer.model.Result;
import com.rebel.transfer.repository.TransferRepo;

import java.util.UUID;

public class TransferService {

    private final TransferRepo transferRepo;

    public TransferService(TransferRepo transferRepo) {
        this.transferRepo = transferRepo;
    }

    public String createNewAccount() {
        Account account = new Account(UUID.randomUUID().toString(), 0L);
        transferRepo.saveAccount(account);
        return account.id();
    }

    public Result getBalance(final String id) {
        return transferRepo.getBalance(id);

    }

    public Result lotteryWinner(String id, Long amount) {
        return transferRepo.addBalance(id, amount);
    }

    public Result transferMoney(String debitAccount, String creditAccount, Long amount) {

        if (!transferRepo.accountExists(debitAccount))
            return Result.fail("Debit account not found");

        if (!transferRepo.accountExists(creditAccount))
            return Result.fail("Credit account not found");

        var debitResult = transferRepo.reduceBalance(debitAccount, amount);
        if (debitResult.failed())
            return debitResult;

        var creditResult = transferRepo.addBalance(creditAccount, amount);
        if (creditResult.failed())
            return creditResult;

        return Result.success();
    }
}
