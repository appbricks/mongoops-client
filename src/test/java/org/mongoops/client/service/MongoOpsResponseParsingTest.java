package org.mongoops.client.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mongoops.client.Application;
import org.mongoops.client.model.*;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebIntegrationTest
public class MongoOpsResponseParsingTest
    extends MongoOpsTestBase {

    @Test
    public void parseAgentLists() {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        MongoOpsClientService clientService = super.mockService.getClientService();
        MockRestServiceServer mockServer = super.mockService.createServer();

        mockServer.expect(requestTo(clientService.groupResourceUrl(GROUP_ID, "agents"))).andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(mockResponse("AllAgents"), MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo(clientService.groupResourceUrl(GROUP_ID, "agents/AUTOMATION"))).andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(mockResponse("AutomationAgents"), MediaType.APPLICATION_JSON));

        AgentCollection agentCollection = clientService.getAgents(GROUP_ID);
        assertEquals(4, agentCollection.getTotalCount());

        Optional<Agent> monitoringAgent = agentCollection.getResults().stream()
            .filter(a -> a.getTypeName().equals("MONITORING")).findFirst();

        assertTrue(monitoringAgent.isPresent());
        assertEquals(59, monitoringAgent.get().getConfCount());
        assertEquals("example", monitoringAgent.get().getHostname());
        assertEquals(true, monitoringAgent.get().isManaged());
        assertEquals("2015-06-18 14:21:42", dateFormat.format(monitoringAgent.get().getLastConf()));
        assertEquals("2015-06-18 14:21:42", dateFormat.format(monitoringAgent.get().getLastPing()));
        assertEquals(6, monitoringAgent.get().getPingCount());
        assertEquals("ACTIVE", monitoringAgent.get().getStateName());

        assertEquals(2, agentCollection.getResults().stream()
            .filter(a -> a.getTypeName().equals("AUTOMATION")).count());

        agentCollection = clientService.getAgents(GROUP_ID, Agent.Type.AUTOMATION);
        assertEquals(3, agentCollection.getTotalCount());

        monitoringAgent = agentCollection.getResults().stream()
            .filter(a -> a.getTypeName().equals("AUTOMATION")).findFirst();

        assertTrue(monitoringAgent.isPresent());
        assertEquals(75, monitoringAgent.get().getConfCount());
        assertEquals("example-1", monitoringAgent.get().getHostname());
        assertEquals(false, monitoringAgent.get().isManaged());
        assertEquals("2015-11-16 19:33:44", dateFormat.format(monitoringAgent.get().getLastConf()));
        assertEquals("NO_PROCESSES", monitoringAgent.get().getStateName());
    }

    @Test
    public void parseAutomationConfig() {

        MongoOpsClientService clientService = super.mockService.getClientService();
        MockRestServiceServer mockServer = super.mockService.createServer();

        mockServer.expect(requestTo(clientService.groupResourceUrl(GROUP_ID, "automationConfig"))).andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(mockResponse("AutomationConfig"), MediaType.APPLICATION_JSON));

        AutomationConfig automationConfig = clientService.getAutomationConfig(GROUP_ID);
        assertEquals(1, automationConfig.getMonitoringVersions().size());
        assertEquals(1, automationConfig.getBackupVersions().size());
        assertEquals(3, automationConfig.getProcesses().size());
        assertEquals(2, automationConfig.getReplicaSets().size());
        assertEquals(1, automationConfig.getSharding().size());
        assertEquals(2, automationConfig.getMongoDbVersions().size());

        Optional<MongoProcess> process = automationConfig.getProcesses().stream().findFirst();
        assertTrue(process.isPresent());
        assertEquals("MyCLUSTER_MySHARD_0_0", process.get().getName());
        assertEquals("mongod", process.get().getProcessType());
        assertEquals("2.6.7", process.get().getVersion());
        assertEquals("testAutoAPI-0.dns.placeholder", process.get().getHostname());
        assertEquals(1, process.get().getAuthSchemaVersion());

        Args arg2_6 = process.get().getArgs2_6();
        assertNotNull(arg2_6);
        assertEquals(27017, arg2_6.get("net.port"));
        assertEquals("/data/MyCLUSTER_MySHARD_0_0", arg2_6.get("storage.dbPath"));
        assertEquals("/data/MyCLUSTER_MySHARD_0_0/mongodb.log", arg2_6.get("systemLog.path"));
        assertEquals("file", arg2_6.get("systemLog.destination"));
        assertEquals("MySHARD_0", arg2_6.get("replication.replSetName"));
    }

    @Test
    public void validateAutomationStateConverged() {

        MongoOpsClientService clientService = super.mockService.getClientService();
        MockRestServiceServer mockServer = super.mockService.createServer();

        mockServer.expect(requestTo(clientService.groupResourceUrl(GROUP_ID, "automationStatus"))).andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(mockResponse("AutomationStatusConverged"), MediaType.APPLICATION_JSON));

        AutomationStatus status = clientService.getAutomationStatus(GROUP_ID);
        assertNotNull(status);

        Set<String> hosts = new HashSet<String>(
            Arrays.asList( "mongo-rs-h1.localdomain", "mongo-rs-h3.localdomain" ));

        assertTrue(status.isConverged(hosts));
    }

    @Test
    public void validateAutomationStateNotConverged() {

        MongoOpsClientService clientService = super.mockService.getClientService();
        MockRestServiceServer mockServer = super.mockService.createServer();

        mockServer.expect(requestTo(clientService.groupResourceUrl(GROUP_ID, "automationStatus"))).andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(mockResponse("AutomationStatusNotConverged"), MediaType.APPLICATION_JSON));

        AutomationStatus status = clientService.getAutomationStatus(GROUP_ID);
        assertNotNull(status);

        Set<String> hosts = new HashSet<String>(
            Arrays.asList( "AGENT_HOST_0", "AGENT_HOST_1" ));

        assertFalse(status.isConverged(hosts));
    }

    private static Resource mockResponse(String name) {
        return new ClassPathResource("mongops/fixtures/ParsingTest/" + name + ".json");
    }
}
