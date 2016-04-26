package org.mongoops.client.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mongoops.client.Application;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebIntegrationTest
public class MongoOpsAutomationTest
    extends MongoOpsTestBase {

    private static final String GROUP_ID = "MongoDB_Group";
    private static final String MONGO_INSTANCE_ID = "MongoDB_Instance_2";

    @Test
    public void deployReplicaSet()
        throws Throwable {

        MockRestServiceServer mockServer = MongoOpsAutomationMocks
            .deployReplicaSet(super.mockService, GROUP_ID, MONGO_INSTANCE_ID);

        super.mockService.getAutomationService().deployReplicaSet(MONGO_INSTANCE_ID, GROUP_ID, ".*-medium-.*", "0.0.0", 5, 3);
        mockServer.verify();
    }

    @Test
    public void updateReplicaSetAndScaleUp()
        throws Throwable {

        MockRestServiceServer mockServer = MongoOpsAutomationMocks
            .updateReplicaSetWithScaleUp(super.mockService, GROUP_ID, MONGO_INSTANCE_ID);

        super.mockService.getAutomationService().deployReplicaSet(MONGO_INSTANCE_ID, GROUP_ID, ".*-medium-.*", "3.0.6", 5, 5);
        mockServer.verify();
    }

    @Test
    public void updateReplicaSetAndScaleDown()
        throws Throwable {

        MockRestServiceServer mockServer = MongoOpsAutomationMocks
            .updateReplicaSetWithScaleDown(super.mockService, GROUP_ID, MONGO_INSTANCE_ID);

        super.mockService.getAutomationService().deployReplicaSet(MONGO_INSTANCE_ID, GROUP_ID, ".*-medium-.*", "0.0.0", 5, 3);
        mockServer.verify();
    }

    @Test
    public void deleteReplicaSet()
        throws Throwable {

        MockRestServiceServer mockServer = MongoOpsAutomationMocks
            .deleteReplicaSet(super.mockService, GROUP_ID, MONGO_INSTANCE_ID);

        super.mockService.getAutomationService().deleteReplicaSet(MONGO_INSTANCE_ID, GROUP_ID);
        mockServer.verify();
    }

    @Test
    public void replicaSetExists()
        throws Throwable {

        MockRestServiceServer mockServer = MongoOpsAutomationMocks
            .replicaSetExists(super.mockService, GROUP_ID, MONGO_INSTANCE_ID);

        assertTrue(super.mockService.getAutomationService().checkReplicaSetExists(MONGO_INSTANCE_ID, GROUP_ID));
        mockServer.verify();
    }
}
