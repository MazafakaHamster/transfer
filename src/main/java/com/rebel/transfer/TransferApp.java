package com.rebel.transfer;

import com.hazelcast.core.Hazelcast;
import com.rebel.transfer.repository.TransferRepo;
import com.rebel.transfer.service.TransferService;
import com.rebel.transfer.web.TransferWebServer;

public class TransferApp {

    private final TransferWebServer webServer;

    public TransferApp() {
        var hazelcast = Hazelcast.newHazelcastInstance();
        var transferRepo = new TransferRepo(hazelcast);
        var transferService = new TransferService(transferRepo);

        this.webServer = new TransferWebServer(8099, transferService);
    }

    void launch() throws Exception {
        webServer.run();
    }
}
