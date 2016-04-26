package org.mongoops.client.service;

import org.springframework.beans.factory.annotation.Autowired;

public class MongoOpsTestBase {

    // Dummy group id
    protected static final String GROUP_ID = "GROUP_ID";

    @Autowired
    protected MockMongoOpsService mockService;
}
