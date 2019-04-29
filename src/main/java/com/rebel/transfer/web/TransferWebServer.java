package com.rebel.transfer.web;

import com.rebel.transfer.service.TransferService;
import com.rebel.transfer.web.router.RouteBuilder;
import com.rebel.transfer.web.router.Router;
import com.rebel.transfer.web.router.exceptions.BadRequestException;
import com.rebel.transfer.web.router.response.Response;

import java.util.concurrent.ExecutorService;

import static com.rebel.transfer.util.JsonUtil.json;

public class TransferWebServer extends NettyWebServerBase {

    private TransferService transferService;

    public TransferWebServer(int port, TransferService transferService) {
        super(port);
        this.transferService = transferService;
    }

    @Override
    protected Router router(ExecutorService executor) {
        return new RouteBuilder(executor)
            .get("/account/balance", request -> {
                var account = request.params.getString("account");

                var result = transferService.getBalance(account);

                if (result.succeeded() && result.value().isPresent()) {
                    request.end(Response.ok(json("balance", result.value().get())));
                } else {
                    fail(result.errorMessage());
                }
            })
            .post("/account/create", request -> {
                var id = transferService.createNewAccount();
                request.end(Response.ok(json("accountId", id)));
            })
            .post("/lotteryWinner", request -> {
                var account = request.params.getString("account");
                var amount = request.params.getLong("amount");
                validate(amount > 0, "Amount less or equals 0");

                var result = transferService.lotteryWinner(account, amount);

                if (result.succeeded()) {
                    request.end(Response.ok(json("message", "Wow, we have a winner here!")));
                } else {
                    fail(result.errorMessage());
                }
            })
            .post("/transfer", request -> {
                var debitAccount = request.params.getString("debit");
                var creditAccount = request.params.getString("credit");
                var amount = request.params.getLong("amount");
                validate(amount > 0, "Amount less or equals 0");
                validate(!debitAccount.equals(creditAccount), "Debit and credit accounts are same");

                var result = transferService.transferMoney(debitAccount, creditAccount, amount);

                if (result.succeeded()) {
                    request.end(Response.ok(json("message", "Transfer successful")));
                } else {
                    fail(result.errorMessage());
                }
            })
            .build();
    }

    private void validate(boolean validation, String message) {
        if (!validation)
            fail(message);
    }

    private void fail(String message) {
        throw new BadRequestException(message);
    }
}