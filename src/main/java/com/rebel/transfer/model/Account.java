package com.rebel.transfer.model;

import java.io.Serializable;
import java.util.Objects;

public class Account implements Serializable {

    private String id;
    private Long   balance;

    public Account(String id, Long balance) {
        this.id = id;
        this.balance = balance;
    }

    public String id() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long balance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return id.equals(account.id) &&
            balance.equals(account.balance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, balance);
    }
}
