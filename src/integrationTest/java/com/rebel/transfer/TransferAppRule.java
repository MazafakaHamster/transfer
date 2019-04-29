package com.rebel.transfer;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.concurrent.CompletableFuture;

public class TransferAppRule implements BeforeEachCallback, AfterEachCallback {

    private TransferApp app;

    @Override
    public void afterEach(ExtensionContext context) {
        app.shutDown();
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        app = new TransferApp();
        var completableFuture = new CompletableFuture<Void>();
        new Thread(() -> app.launch(completableFuture)).start();
        completableFuture.get();
    }
}
