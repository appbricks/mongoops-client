package org.mongoops.client.service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MockMongoAgentRegistry
    implements MongoAgentRegistry {

    @Override
    public String getAvailabilityZone(String hostname) {
        return null;
    }

    @Override
    public String getServiceType(String hostname) {
        return null;
    }

    @Override
    public List<String> getAvailabilityZones() {
        return null;
    }

    @Override
    public List<String> getServiceTypes() {
        return null;
    }
}
