package com.rebel.transfer.web;

import com.rebel.transfer.service.TransferService;
import com.rebel.transfer.web.router.RouteBuilder;
import com.rebel.transfer.web.router.Router;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

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
                    return response(HttpResponseStatus.OK, "Balance: " + result.value().get());
                } else {
                    return response(HttpResponseStatus.BAD_REQUEST, result.errorMessage());
                }
            })
            .post("/account/create", request -> {
                var id = transferService.createNewAccount();
                return response(HttpResponseStatus.OK, "Account ID: " + id);
            })
            .post("/lotteryWinner", request -> {
                var account = request.params.getString("account");
                var amount = request.params.getLong("amount");

                var result = transferService.lotteryWinner(account, amount);

                if (result.succeeded()) {
                    return response(HttpResponseStatus.OK, "Wow, we have a winner here!");
                } else {
                    return response(HttpResponseStatus.BAD_REQUEST, result.errorMessage());
                }
            })
            .post("/transfer", request -> {
                var debitAccount = request.params.getString("debit");
                var creditAccount = request.params.getString("credit");
                var amount = request.params.getLong("amount");

                var result = transferService.transferMoney(debitAccount, creditAccount, amount);

                if (result.succeeded()) {
                    return response(HttpResponseStatus.OK, "Success!");
                } else {
                    return response(HttpResponseStatus.BAD_REQUEST, result.errorMessage());
                }
            })
            .build();
    }

    private FullHttpResponse response(HttpResponseStatus status, String body) {
        ByteBuf content = Unpooled.copiedBuffer(body, CharsetUtil.UTF_8);
        FullHttpResponse response = new DefaultFullHttpResponse(
            HttpVersion.HTTP_1_1,
            status,
            content
        );
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());
        return response;
    }
}