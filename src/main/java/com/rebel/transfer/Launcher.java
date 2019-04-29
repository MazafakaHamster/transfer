package com.rebel.transfer;

import lombok.SneakyThrows;

import java.util.concurrent.CompletableFuture;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Launcher {

    private static final Logger logger = Logger.getLogger(Launcher.class.getName());

    @SneakyThrows
    private static void init() {
        LogManager.getLogManager().readConfiguration(Launcher.class.getResourceAsStream("/logging.properties"));
    }

    public static void main(String[] args) {
        init();
        launch();
    }

    private static void launch() {
        var app = new TransferApp();
        var future = new CompletableFuture<Void>();
        future.thenRun(() -> logger.info("Application started"));
        app.launch(future);
    }
}
