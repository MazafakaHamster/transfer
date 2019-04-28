package com.rebel.transfer.model;

import java.io.Serializable;

public class Operation implements Serializable {
    private String id;
    private Long   timestamp;
    private String debitAccount;
    private String creditAccount;

    public Operation(String id, Long timestamp, String debitAccount, String creditAccount) {
        this.id = id;
        this.timestamp = timestamp;
        this.debitAccount = debitAccount;
        this.creditAccount = creditAccount;
    }

    public String id() {
        return id;
    }

    public Long timestamp() {
        return timestamp;
    }

    public String debitAccount() {
        return debitAccount;
    }

    public String creditAccount() {
        return creditAccount;
    }
}
