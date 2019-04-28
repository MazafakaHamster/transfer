package com.rebel.transfer;

import java.io.IOException;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Launcher {

    private static Logger logger = Logger.getLogger(Launcher.class.getName());

    public static void main(String[] args) throws Exception {
        init();
        var app = new TransferApp();
        app.launch();
    }

    private static void init() throws IOException {
        LogManager.getLogManager().readConfiguration(Launcher.class.getResourceAsStream("/logging.properties"));
    }
}
