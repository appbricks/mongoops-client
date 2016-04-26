package org.mongoops.client.service;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public final class MongoOpsAutomationMocks {

    public static MockRestServiceServer deployReplicaSet(
        MockMongoOpsService mockService,
        String groupId, Object... args)
        throws Throwable {

        MongoOpsClientService clientService = mockService.getClientService();
        MockRestServiceServer mockServer = mockService.createServer();

        mockServer.expect(requestTo(clientService.groupResourceUrl(groupId, "automationConfig")))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(MockHttpRequest.mockJsonPayload("creation", "GetAutomationConfig1", args),
                MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo(clientService.groupResourceUrl(groupId, "automationStatus")))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(MockHttpRequest.mockJsonPayload("creation", "AutomationStatus", args),
                MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo(clientService.groupResourceUrl(groupId, "automationConfig")))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(MockHttpRequest.mockJsonPayload("creation", "GetAutomationConfig1", args),
                MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo(clientService.groupResourceUrl(groupId, "agents/AUTOMATION")))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(MockHttpRequest.mockJsonPayload("creation", "AutomationAgents1", args),
                MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo(clientService.groupResourceUrl(groupId, "automationConfig")))
            .andExpect(new MockHttpRequest(HttpMethod.PUT, "creation", "PutAutomationConfig1_Req", args))
            .andRespond(withSuccess(MockHttpRequest.mockJsonPayload("creation", "PutAutomationConfig_Resp", args),
                MediaType.APPLICATION_JSON));

        return mockServer;
    }

    public static void addReplicaSetUpdateAndStatusMocks(
        MockRestServiceServer mockServer,
        MockMongoOpsService mockService,
        String groupId, Object... args)
        throws Throwable {

        MongoOpsClientService clientService = mockService.getClientService();

        // Create async status
        mockServer.expect(requestTo(clientService.groupResourceUrl(groupId, "automationStatus")))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(MockHttpRequest.mockJsonPayload("convergence", "AutomationStatus6", args),
                MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo(clientService.groupResourceUrl(groupId, "automationConfig")))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(MockHttpRequest.mockJsonPayload("creation", "PutAutomationConfig1_Req", args),
                MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo(clientService.groupResourceUrl(groupId, "automationConfig")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(MockHttpRequest.mockJsonPayload("creation", "PutAutomationConfig1_Req", args),
                        MediaType.APPLICATION_JSON));

        // Update
        mockServer.expect(requestTo(clientService.groupResourceUrl(groupId, "automationConfig")))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(MockHttpRequest.mockJsonPayload("creation", "PutAutomationConfig1_Req", args),
                MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo(clientService.groupResourceUrl(groupId, "automationConfig")))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(MockHttpRequest.mockJsonPayload("creation", "PutAutomationConfig1_Req", args),
                MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo(clientService.groupResourceUrl(groupId, "automationStatus")))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(MockHttpRequest.mockJsonPayload("creation", "AutomationStatus", args),
                MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo(clientService.groupResourceUrl(groupId, "automationConfig")))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(MockHttpRequest.mockJsonPayload("creation", "PutAutomationConfig1_Req", args),
                MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo(clientService.groupResourceUrl(groupId, "agents/AUTOMATION")))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(MockHttpRequest.mockJsonPayload("creation", "AutomationAgents2", args),
                MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo(clientService.groupResourceUrl(groupId, "automationConfig")))
            .andExpect(new MockHttpRequest(HttpMethod.PUT, "creation", "PutAutomationConfig2_Req", args))
            .andRespond(withSuccess(MockHttpRequest.mockJsonPayload("creation", "PutAutomationConfig_Resp", args),
                MediaType.APPLICATION_JSON));

        // Update async status
        mockServer.expect(requestTo(clientService.groupResourceUrl(groupId, "automationStatus")))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(MockHttpRequest.mockJsonPayload("convergence", "AutomationStatus6", args),
                MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo(clientService.groupResourceUrl(groupId, "automationConfig")))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(MockHttpRequest.mockJsonPayload("creation", "PutAutomationConfig2_Req", args),
                MediaType.APPLICATION_JSON));
    }

    public static void addReplicaSetFailedStatusMocks(
        MockRestServiceServer mockServer,
        MockMongoOpsService mockService,
        String groupId, Object... args)
        throws Throwable {

        MongoOpsClientService clientService = mockService.getClientService();

        mockServer.expect(requestTo(clientService.groupResourceUrl(groupId, "automationStatus")))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(MockHttpRequest.mockJsonPayload("convergence", "AutomationStatus5", args),
                MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo(clientService.groupResourceUrl(groupId, "automationConfig")))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(MockHttpRequest.mockJsonPayload("creation", "PutAutomationConfig1_Req", args),
                MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo(clientService.groupResourceUrl(groupId, "automationConfig")))
            .andExpect(new MockHttpRequest(HttpMethod.PUT, "creation", "RollbackAutomationConfig", args))
            .andRespond(withSuccess(MockHttpRequest.mockJsonPayload("creation", "PutAutomationConfig_Resp", args),
                MediaType.APPLICATION_JSON));
    }

    public static MockRestServiceServer updateReplicaSetWithScaleUp(
        MockMongoOpsService mockService,
        String groupId, Object... args)
        throws Throwable {

        MongoOpsClientService clientService = mockService.getClientService();
        MockRestServiceServer mockServer = mockService.createServer();

        mockServer.expect(requestTo(clientService.groupResourceUrl(groupId, "automationConfig")))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(MockHttpRequest.mockJsonPayload("creation", "PutAutomationConfig1_Req", args),
                MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo(clientService.groupResourceUrl(groupId, "automationStatus")))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(MockHttpRequest.mockJsonPayload("creation", "AutomationStatus", args),
                MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo(clientService.groupResourceUrl(groupId, "automationConfig")))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(MockHttpRequest.mockJsonPayload("creation", "PutAutomationConfig1_Req", args),
                MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo(clientService.groupResourceUrl(groupId, "agents/AUTOMATION")))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(MockHttpRequest.mockJsonPayload("creation", "AutomationAgents2", args),
                MediaType.APPLICATION_JSON));

        mockServer.expect(requestTo(clientService.groupResourceUrl(groupId, "automationConfig")))
            .andExpect(new MockHttpRequest(HttpMethod.PUT, "creation", "PutAutomationConfig3_Req", args))
            .andRespond(withSuccess(MockHttpRequest.mockJsonPayload("creation", "PutAutomationConfig_Resp", args),
                    MediaType.APPLICATION_JSON));

        return mockServer;
    }

    public static MockRestServiceServer updateReplicaSetWithScaleDown(
            MockMongoOpsService mockService,
            String groupId, Object... args)
            throws Throwable {

        MongoOpsClientService clientService = mockService.getClientService();
        MockRestServiceServer mockServer = mockService.createServer();

        mockServer.expect(requestTo(clientService.groupResourceUrl(groupId, "automationConfig")))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(MockHttpRequest.mockJsonPayload("creation", "PutAutomationConfig3_Req", args),
                MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo(clientService.groupResourceUrl(groupId, "automationStatus")))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(MockHttpRequest.mockJsonPayload("creation", "AutomationStatus", args),
                MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo(clientService.groupResourceUrl(groupId, "automationConfig")))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(MockHttpRequest.mockJsonPayload("creation", "PutAutomationConfig3_Req", args),
                MediaType.APPLICATION_JSON));

        // Scale down
        mockServer.expect(requestTo(clientService.groupResourceUrl(groupId, "automationConfig")))
            .andExpect(new MockHttpRequest(HttpMethod.PUT, "creation", "PutAutomationConfig4_Req", args))
            .andRespond(withSuccess(MockHttpRequest.mockJsonPayload("creation", "PutAutomationConfig_Resp", args),
                MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo(clientService.groupResourceUrl(groupId, "automationConfig")))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(MockHttpRequest.mockJsonPayload("creation", "PutAutomationConfig4_Req", args),
                MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo(clientService.groupResourceUrl(groupId, "automationStatus")))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(MockHttpRequest.mockJsonPayload("creation", "AutomationStatus", args),
                MediaType.APPLICATION_JSON));

        mockServer.expect(requestTo(clientService.groupResourceUrl(groupId, "agents/AUTOMATION")))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(MockHttpRequest.mockJsonPayload("creation", "AutomationAgents3", args),
                MediaType.APPLICATION_JSON));

        mockServer.expect(requestTo(clientService.groupResourceUrl(groupId, "automationConfig")))
            .andExpect(new MockHttpRequest(HttpMethod.PUT, "creation", "PutAutomationConfig1_Req", args))
            .andRespond(withSuccess(MockHttpRequest.mockJsonPayload("creation", "PutAutomationConfig_Resp", args),
                MediaType.APPLICATION_JSON));

        return mockServer;
    }

    public static MockRestServiceServer deleteReplicaSet(
        MockMongoOpsService mockService, String groupId, Object... args)
        throws Throwable {

        MongoOpsClientService clientService = mockService.getClientService();
        MockRestServiceServer mockServer = mockService.createServer();

        mockServer.expect(requestTo(clientService.groupResourceUrl(groupId, "automationConfig")))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(MockHttpRequest.mockJsonPayload("creation", "PutAutomationConfig1_Req", args),
                MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo(clientService.groupResourceUrl(groupId, "automationStatus")))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(MockHttpRequest.mockJsonPayload("convergence", "AutomationStatus1", args),
                MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo(clientService.groupResourceUrl(groupId, "automationConfig")))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(MockHttpRequest.mockJsonPayload("creation", "PutAutomationConfig1_Req", args),
                MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo(clientService.groupResourceUrl(groupId, "automationConfig")))
            .andExpect(new MockHttpRequest(HttpMethod.PUT, "deletion", "PutAutomationConfig1_Req", args))
            .andRespond(withSuccess(MockHttpRequest.mockJsonPayload("deletion", "PutAutomationConfig1_Resp", args),
                MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo(clientService.groupResourceUrl(groupId, "automationConfig")))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(MockHttpRequest.mockJsonPayload("creation", "PutAutomationConfig1_Req", args),
                MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo(clientService.groupResourceUrl(groupId, "automationStatus")))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(MockHttpRequest.mockJsonPayload("convergence", "AutomationStatus2", args),
                MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo(clientService.groupResourceUrl(groupId, "automationStatus")))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(MockHttpRequest.mockJsonPayload("convergence", "AutomationStatus3", args),
                MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo(clientService.groupResourceUrl(groupId, "automationStatus")))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(MockHttpRequest.mockJsonPayload("convergence", "AutomationStatus4", args),
                MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo(clientService.groupResourceUrl(groupId, "automationStatus")))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(MockHttpRequest.mockJsonPayload("convergence", "AutomationStatus5", args),
                MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo(clientService.groupResourceUrl(groupId, "automationStatus")))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(MockHttpRequest.mockJsonPayload("convergence", "AutomationStatus6", args),
                MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo(clientService.groupResourceUrl(groupId, "automationConfig")))
            .andExpect(new MockHttpRequest(HttpMethod.PUT, "deletion", "PutAutomationConfig2_Req", args))
            .andRespond(withSuccess(MockHttpRequest.mockJsonPayload("deletion", "PutAutomationConfig2_Resp", args),
                MediaType.APPLICATION_JSON));

        return mockServer;
    }

    public static MockRestServiceServer replicaSetExists(
        MockMongoOpsService mockService, String groupId, Object... args)
        throws Throwable {

        MongoOpsClientService clientService = mockService.getClientService();
        MockRestServiceServer mockServer = mockService.createServer();

        mockServer.expect(requestTo(clientService.groupResourceUrl(groupId, "automationConfig")))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(MockHttpRequest.mockJsonPayload("creation", "PutAutomationConfig1_Req", args),
                MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo(clientService.groupResourceUrl(groupId, "automationStatus")))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(MockHttpRequest.mockJsonPayload("convergence", "AutomationStatus6", args),
                MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo(clientService.groupResourceUrl(groupId, "automationConfig")))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(MockHttpRequest.mockJsonPayload("creation", "PutAutomationConfig1_Req", args),
                MediaType.APPLICATION_JSON));

        return mockServer;
    }
}
