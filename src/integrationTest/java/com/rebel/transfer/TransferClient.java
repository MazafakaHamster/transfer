package com.rebel.transfer;

import lombok.SneakyThrows;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;

@ExtendWith(TransferAppRule.class)
class TransferClient {

    private final HttpClient client;

    TransferClient(HttpClient client) {
        this.client = client;
    }

    @SneakyThrows
    String createAccount() {
        var response = client.post("/account/create");
        return response.jsonContent().getString("accountId");
    }

    @SneakyThrows
    void lotteryWinner(String accountId, Long amount) {
        client.post("/lotteryWinner", Map.of("account", accountId, "amount", amount.toString()));
    }

    @SneakyThrows
    void transfer(String debitId, String creditId, Long amount) {
        client.post("/transfer", Map.of(
            "debit", debitId,
            "credit", creditId,
            "amount", amount.toString()
            )
        );
    }

    @SneakyThrows
    Long getBalance(String accountId) {
        var response = client.get("/account/balance", Map.of("account", accountId));
        return response.jsonContent().getLong("balance");
    }
}
