package com.rebel.transfer.repository;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.rebel.transfer.model.Account;
import com.rebel.transfer.model.Operation;
import com.rebel.transfer.model.Result;

import static java.util.Objects.isNull;

public class TransferRepo {

    private final IMap<String, Account>    accountMap;
    private final IMap<String, Operation> operationMap;

    public TransferRepo(HazelcastInstance instance) {
        this.accountMap = instance.getMap("account");
        this.operationMap = instance.getMap("operation");
    }

    public Account loadAccount(String id) {
        return accountMap.get(id);
    }

    public void saveAccount(Account account) {
        accountMap.put(account.id(), account);
    }

    public void saveOperation(Operation operation) {
        operationMap.put(operation.id(), operation);
    }

    public boolean accountExists(String id) {
        return accountMap.containsKey(id);
    }

    public Result addBalance(String id, Long amount) {
        var account = loadAccount(id);

        if (isNull(account))
            return Result.fail("Debit account not found");

        account.setBalance(account.balance() + amount);

        saveAccount(account);
        return Result.success();
    }

    public Result reduceBalance(String id, Long amount) {
        var account = loadAccount(id);

        if (isNull(account))
            return Result.fail("Debit account not found");

        account.setBalance(account.balance() - amount);

        if (account.balance() < 0)
            return Result.fail("Not enough funds");

        saveAccount(account);
        return Result.success();
    }

    public Result<Long> getBalance(String id) {

        var account = loadAccount(id);

        if (isNull(account))
            return Result.fail("Account not found");

        return Result.success(account.balance());
    }
}
