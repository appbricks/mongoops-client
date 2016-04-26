package org.mongoops.client.service;

import org.apache.http.HttpHost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

@Service
public class MockMongoOpsService {

    private MongoOpsAutomationService automationService;
    private String mongoOpsApiPath;

    private MongoOpsClientService clientService;
    private RestTemplate restTemplate;

    @Autowired
    public void MockMongoOpsService(MongoOpsAutomationService automationService, String mongoOpsApiPath) {

        this.automationService = automationService;
        this.mongoOpsApiPath = mongoOpsApiPath;

        this.clientService = new MongoOpsClientService(
                new HttpHost("mongo-ops-host.some-domain.com", 8080, "http"), this.mongoOpsApiPath );

        this.restTemplate = new RestTemplate();
        clientService.setMongoOpsRestTemplate(this.restTemplate);

        this.automationService.setClientService(clientService);
    }

    public MongoOpsAutomationService getAutomationService() {
        return this.automationService;
    }

    public MongoOpsClientService getClientService() {
        return this.clientService;
    }

    public MockRestServiceServer createServer() {
        return MockRestServiceServer.createServer(this.restTemplate);
    }
}
