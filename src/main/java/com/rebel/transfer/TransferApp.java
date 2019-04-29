package com.rebel.transfer;

import com.hazelcast.core.Hazelcast;
import com.rebel.transfer.repository.TransferRepo;
import com.rebel.transfer.service.TransferService;
import com.rebel.transfer.web.TransferWebServer;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class TransferApp {

    private final Config config = ConfigFactory.defaultApplication();

    private final TransferWebServer webServer;

    public TransferApp() {
        var hazelcast = Hazelcast.newHazelcastInstance();
        var transferRepo = new TransferRepo(hazelcast);
        var transferService = new TransferService(transferRepo);
        this.webServer = new TransferWebServer(config.getInt("rest-port"), transferService);
    }

    void launch() throws Exception {
        webServer.run();
    }
}
