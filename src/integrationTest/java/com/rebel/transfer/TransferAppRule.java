package com.rebel.transfer;

import lombok.SneakyThrows;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.concurrent.CompletableFuture;
import java.util.logging.LogManager;

public class TransferAppRule implements BeforeAllCallback, BeforeEachCallback, AfterEachCallback {

    @SneakyThrows
    private static void init() {
        LogManager.getLogManager().readConfiguration(Launcher.class.getResourceAsStream("/logging.properties"));
    }

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

    @Override
    public void beforeAll(ExtensionContext context) {
        init();
    }
}
