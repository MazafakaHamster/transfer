package com.rebel.transfer;

import com.rebel.transfer.web.TransferWebServer;

public class TransferApp {

    private final TransferWebServer webServer;

    public TransferApp() {
        this.webServer = new TransferWebServer(8099);
    }

    void launch() throws Exception {
        webServer.run();

    }
}
