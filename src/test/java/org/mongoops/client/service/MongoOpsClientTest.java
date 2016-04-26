package org.mongoops.client.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mongoops.client.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebIntegrationTest
public class MongoOpsClientTest {

    @Autowired
    private MongoOpsClientService clientService;

    @Value("${mongodb.om.groupId}")
    private String groupId;

    @Before
    public void setup() {
    }

    @Test
    public void test() {
    }
}
