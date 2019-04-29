package com.rebel.transfer.web;

import com.rebel.transfer.service.TransferService;
import com.rebel.transfer.web.router.RouteBuilder;
import com.rebel.transfer.web.router.Router;
import com.rebel.transfer.web.router.response.Response;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TransferWebServer extends NettyWebServerBase {

    private TransferService transferService;

    public TransferWebServer(int port, TransferService transferService) {
        super(port);
        this.transferService = transferService;
    }

    @Override
    protected Router router() {
        return new RouteBuilder()
            .get("/account/balance", request -> {
                var account = request.params.getString("account");
                var result = transferService.getBalance(account);

                if (result.succeeded() && result.value().isPresent()) {
                    return Response.ok("Balance: " + result.value().get());
                } else {
                    return Response.custom(HttpResponseStatus.BAD_REQUEST, result.errorMessage());
                }
            })
            .post("/account/create", request -> {
                var id = transferService.createNewAccount();
                return Response.ok("Account ID: " + id);
            })
            .post("/lotteryWinner", request -> {
                var account = request.params.getString("account");
                var amount = request.params.getLong("amount");

                var result = transferService.lotteryWinner(account, amount);

                if (result.succeeded()) {
                    return Response.ok("Wow, we have a winner here!");
                } else {
                    return Response.custom(HttpResponseStatus.BAD_REQUEST, result.errorMessage());
                }
            })
            .post("/transfer", request -> {
                var debitAccount = request.params.getString("debit");
                var creditAccount = request.params.getString("credit");
                var amount = request.params.getLong("amount");

                var result = transferService.transferMoney(debitAccount, creditAccount, amount);

                if (result.succeeded()) {
                    return Response.ok("Success!");
                } else {
                    return Response.custom(HttpResponseStatus.BAD_REQUEST, result.errorMessage());
                }
            })
            .build();
    }
}