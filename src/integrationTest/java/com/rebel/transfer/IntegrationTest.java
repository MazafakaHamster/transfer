package com.rebel.transfer;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(TransferAppRule.class)
class IntegrationTest {

    private final HttpClient client;
    private final TransferClient transferClient;

    IntegrationTest() {
        Config config = ConfigFactory.defaultApplication();
        this.client = new HttpClient("localhost", config.getInt("rest-port"));
        this.transferClient = new TransferClient(client);
    }

    @Test
    void createAccountTest() {
        var response = client.post("/account/create");
        status(200, response);
        assertTrue(response.jsonContent().has("accountId"), "Response without message");
    }

    @Test
    void balanceTest() {
        var account = transferClient.createAccount();

        var response = client.get("/account/balance", Map.of("account", account));
        status(200, response);
        assertTrue(response.jsonContent().has("balance"), "No balance in response");
        assertEquals(0, response.jsonContent().getInt("balance"), "Non 0 balance of new account");
    }

    @Test
    void incorrectAccountBalanceTest() {
        var response = client.get("/account/balance", Map.of("account", "hello"));
        errorMessage(response);
    }

    @Test
    void lotteryWinnerTest() {
        var account = transferClient.createAccount();

        var balance = transferClient.getBalance(account);

        assertEquals(0, balance, "Non 0 balance of new account");

        var lotteryWinAmount = 500L;

        var response = client.post("/lotteryWinner", Map.of(
            "account", account,
            "amount", Long.toString(lotteryWinAmount)
        ));

        status(200, response);
        assertTrue(response.jsonContent().has("message"), "No message in response");
        assertEquals(lotteryWinAmount, transferClient.getBalance(account), "Wrong new balance");
    }

    @Test
    void incorrectAmountLotteryWinnerTest() {
        var account = transferClient.createAccount();

        var lotteryWinAmount = -50L;

        var response = client.post("/lotteryWinner", Map.of(
            "account", account,
            "amount", Long.toString(lotteryWinAmount)
        ));

        errorMessage(response);
    }

    @Test
    void incorrectAccountLotteryWinnerTest() {
        var lotteryWinAmount = 500L;

        var response = client.post("/lotteryWinner", Map.of(
            "account", "hello",
            "amount", Long.toString(lotteryWinAmount)
        ));

        errorMessage(response);
    }

    @Test
    void transferTest() {
        var debitAccount = transferClient.createAccount();
        var creditAccount = transferClient.createAccount();

        var debitAccountBalance = 150L;

        transferClient.lotteryWinner(debitAccount, debitAccountBalance);

        var transferAmount = 45L;

        var response = client.post("/transfer", Map.of(
            "debit", debitAccount,
            "credit", creditAccount,
            "amount", Long.toString(transferAmount)
        ));

        status(200, response);
        assertEquals(
            "Transfer successful", response.jsonContent().getString("message"),
            "Incorrect message"
        );

        balance(debitAccountBalance - transferAmount, debitAccount);
        balance(transferAmount, creditAccount);
    }

    @Test
    void sameAccountTransferTest() {
        var debitAccount = transferClient.createAccount();

        var debitAccountBalance = 150L;

        transferClient.lotteryWinner(debitAccount, debitAccountBalance);

        var transferAmount = 45L;

        var response = client.post("/transfer", Map.of(
            "debit", debitAccount,
            "credit", debitAccount,
            "amount", Long.toString(transferAmount)
        ));

        errorMessage(response);

        balance(debitAccountBalance, debitAccount);
    }

    @Test
    void chainTransferTest() {
        var account1 = transferClient.createAccount();
        var account2 = transferClient.createAccount();
        var account3 = transferClient.createAccount();
        var account4 = transferClient.createAccount();
        var account5 = transferClient.createAccount();

        var initialAmount = 150L;
        transferClient.lotteryWinner(account1, initialAmount);

        var transfer1 = 95L;
        transferClient.transfer(account1, account2, transfer1);
        balance(transfer1, account2);

        var transfer2 = 72L;
        transferClient.transfer(account2, account3, transfer2);
        balance(transfer2, account3);

        var transfer3 = 69L;
        transferClient.transfer(account3, account4, transfer3);
        balance(transfer3, account4);

        var transfer4 = 42L;
        transferClient.transfer(account4, account5, transfer4);
        balance(transfer4, account5);

        balance(initialAmount, account1, account2, account3, account4, account5);
    }

    @Test
    void incorrectCreditAccountTransferTest() {
        var debitAccount = transferClient.createAccount();

        var debitAccountBalance = 150L;

        transferClient.lotteryWinner(debitAccount, debitAccountBalance);

        var transferAmount = 45L;

        var response = client.post("/transfer", Map.of(
            "debit", debitAccount,
            "credit", "hello",
            "amount", Long.toString(transferAmount)
        ));

        errorMessage(response);
        assertEquals(
            debitAccountBalance,
            transferClient.getBalance(debitAccount),
            "Wrong debit account balance"
        );
    }

    @Test
    void incorrectDebitAccountTransferTest() {
        var creditAccount = transferClient.createAccount();

        var transferAmount = 45L;

        var response = client.post("/transfer", Map.of(
            "debit", "hello",
            "credit", creditAccount,
            "amount", Long.toString(transferAmount)
        ));

        errorMessage(response);
        assertEquals(0, transferClient.getBalance(creditAccount), "Wrong credit account balance");
    }

    @Test
    void negativeAmountTransferTest() {
        var debitAccount = transferClient.createAccount();
        var creditAccount = transferClient.createAccount();

        var debitAccountBalance = 150L;

        transferClient.lotteryWinner(debitAccount, debitAccountBalance);

        var transferAmount = -45L;

        var response = client.post("/transfer", Map.of(
            "debit", debitAccount,
            "credit", creditAccount,
            "amount", Long.toString(transferAmount)
        ));

        errorMessage(response);
        assertEquals(debitAccountBalance, transferClient.getBalance(debitAccount), "Wrong debit account balance");
        assertEquals(0, transferClient.getBalance(creditAccount), "Wrong credit account balance");
    }

    @Test
    void noParamsTest() {
        var response = client.get("/account/balance");
        errorMessage(response);
    }

    @Test
    void wrongParamTypeTest() {
        var response = client.post("/lotteryWinner", Map.of(
            "account", "hello",
            "amount", "world"
        ));

        errorMessage(response);
    }

    private void balance(Long balance, String... accounts) {
        var totalBalance = Arrays.stream(accounts).mapToLong(transferClient::getBalance).sum();
        assertEquals(balance, totalBalance, "Wrong account balance");
    }

    private void status(int status, HttpClient.Response response) {
        assertEquals(status, response.statusCode(), "Wrong response status code");
    }

    private void errorMessage(HttpClient.Response response) {
        status(400, response);
        assertTrue(response.jsonContent().has("errorMessage"), "No error message");
    }
}
