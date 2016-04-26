package org.mongoops.client.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mongoops.client.exeption.*;
import org.mongoops.client.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * MongoDB Enterprise Ops Manager deployment service
 */
@Service
public class MongoOpsAutomationService {

    private static final Log log = LogFactory.getLog(MongoOpsAutomationService.class);

    private EmailAlertService emailAlertService;

    private MongoOpsClientService clientService;
    private MongoAgentRegistry agentRegistry;

    private String dbAdminUser;
    private String dbAdminPassword;

    @Value("${process.args2_6_template:mongoops/templates/args2_6.json}")
    private String args2_6_template;

    @Value("${process.mongod.port:30000}")
    private int mongodPort;

    @Value("${process.mongod.data-path:/data/mongodb}")
    private String mongodDataPath;

    @Value("${mongodb.replicaset.alert.remainingNodes}")
    private int alertRemainingNodes;

    private Pattern nodeAzPattern;

    @Autowired
    public MongoOpsAutomationService(
        EmailAlertService emailAlertService,
        MongoOpsClientService clientService,
        MongoAgentRegistry agentRegistry,
        String mongoOpsDbAdminUser, String mongoOpsDbAdminPassword,
        @Value("${automation.nodeAzPattern:}") String nodeAzPattern ) {

        this.emailAlertService = emailAlertService;
        this.clientService = clientService;
        this.agentRegistry = agentRegistry;
        this.dbAdminUser = mongoOpsDbAdminUser;
        this.dbAdminPassword = mongoOpsDbAdminPassword;

        if (!nodeAzPattern.isEmpty()) {
            this.nodeAzPattern = Pattern.compile(nodeAzPattern);
        }
    }

    public MongoOpsClientService getClientService() {
        return clientService;
    }

    public void setClientService(MongoOpsClientService clientService) {
        this.clientService = clientService;
    }

    @SuppressWarnings("unchecked")
    public String deployReplicaSet(
        String name,
        String groupId,
        String pattern,
        String dbVersion,
        int authSchemaVersion,
        int nodeCount )
        throws MongoOpsException {

        this.waitForConvergence(groupId, 60);

        // Retrieve group's current configuration
        AutomationConfig automationConfig = this.clientService.getAutomationConfig(groupId);

        String lastKnowGoodConfig = null;
        try {
            StringWriter out = new StringWriter();
            new ObjectMapper().writeValue(out, automationConfig);
            lastKnowGoodConfig = out.toString();

        } catch (Throwable t) {

            log.error(
                String.format("Unable to serialize current good automation config document: %s",
                t.getMessage()), t );
        }

        if (dbVersion.isEmpty() || dbVersion.equals("0.0.0")) {

            Optional<MongoDbVersion> latestVersion = automationConfig
                .getMongoDbVersions().stream().sorted().findFirst();

            if (!latestVersion.isPresent()) {
                throw new MongoOpsDeploymentException(
                    "Unable to determine the latest mongodb version for replica set '%s' as " +
                    "the automation config did not return the list of available versions.", name );
            }
            dbVersion = latestVersion.get().getName();
        }

        this.updateReplicaSet(automationConfig, name, groupId,
            pattern, dbVersion, authSchemaVersion, nodeCount);
        this.sendAutomationConfig(name, groupId, automationConfig);

        return lastKnowGoodConfig;
    }

    public void deleteReplicaSet(String name, String groupId)
        throws MongoOpsException {

        this.waitForConvergence(groupId, 60);

        // Retrieve group's current configuration
        AutomationConfig automationConfig = this.clientService.getAutomationConfig(groupId);

        // Remove all agents and processes associated with replica set
        this.removeReplicaSetAgents(automationConfig, name, groupId, -1);

        log.debug(String.format("Removing members in replica set '%s'.", name));
        this.sendAutomationConfig(name, groupId, automationConfig);
    }

    public boolean checkReplicaSetExists(String name, String groupId)
        throws MongoOpsException {

        this.waitForConvergence(groupId, 60);

        // Retrieve group's current configuration
        AutomationConfig automationConfig = this.clientService.getAutomationConfig(groupId);

        String processNamePrefix = String.format("%s_", name);
        Set<String> processes = automationConfig.getProcesses().stream()
            .filter(p -> p.getName().startsWith(processNamePrefix) && !p.isDisabled())
            .map(p -> p.getName())
            .collect(Collectors.toSet());

        Optional<ReplicaSet> replicaSet = automationConfig.getReplicaSets().stream()
            .filter(r -> r.get_id().equals(name))
            .findFirst();

        if (!replicaSet.isPresent()) {
            return false;
        }

        return replicaSet.get().getMembers().stream()
            .map(m -> processes.contains(m.getHost()))
            .reduce(true, (result, contains) -> result && contains);
    }

    public String getReplicaSetStatus(String name, String groupId)
        throws MongoOpsException {

        AutomationStatus status = this.clientService.getAutomationStatus(groupId);

        // Retrieve group's current configuration
        AutomationConfig automationConfig = this.clientService.getAutomationConfig(groupId);

        String processNamePrefix = String.format("%s_", name);
        Set<String> processes = automationConfig.getProcesses().stream()
            .filter(p -> p.getName().startsWith(processNamePrefix) && !p.isDisabled())
            .map(p -> p.getName())
            .collect(Collectors.toSet());

        Optional<ReplicaSet> replicaSet = automationConfig.getReplicaSets().stream()
            .filter(r -> r.get_id().equals(name))
            .findFirst();

        // Throw exception if replica set does not exist or at
        // least one node in the replicaset is not mapped to a process
        if ( !replicaSet.isPresent() ||
            !replicaSet.get().getMembers().stream()
                .map(m -> processes.contains(m.getHost()))
                .reduce(false, (result, contains) -> result || contains) ) {

            throw new MongoOpsReplicaSetNotFound(
                String.format("Mongo replica set '%s' was not found for group '%s'.",
                name, groupId) );
        }

        if (!status.isConverged(automationConfig.getHosts())) {

            return status.getProcesses().stream()
                .filter(p -> p.getName().startsWith(processNamePrefix))
                .map(p -> {
                    StringBuilder sb = new StringBuilder(p.getName());
                    sb.append(':').append(p.getHostname());
                    String[] plan = p.getPlan();
                    if (plan.length > 0) {
                        sb.append('[').append(String.join("; ", p.getPlan())).append(']');
                    }
                    return sb.toString();
                })
                .collect(Collectors.joining(", ", "Provisioning nodes ", "..."));
        }

        return null;
    }

    public String getReplicaSetMongoDbVersion(String name, String groupId)
        throws MongoOpsReplicaSetNotFound {

        // Retrieve group's current configuration
        AutomationConfig automationConfig = this.clientService.getAutomationConfig(groupId);
        List<MongoProcess> processes = automationConfig.getProcesses(name);
        if (processes.size() > 0) {
            return processes.get(0).getVersion();
        }
        throw new MongoOpsReplicaSetNotFound(String.format("No processes were foung for replicaset '%s'.", name));
    }

    public MongoClient getMongoClient(String name, String groupId)
        throws MongoOpsException {

        // Retrieve group's current configuration
        AutomationConfig automationConfig = this.clientService.getAutomationConfig(groupId);

        try {
            String processNamePrefix = String.format("%s_", name);

            List<MongoProcess> processes = automationConfig.getProcesses().stream()
                .filter(p -> p.getName().startsWith(processNamePrefix) && !p.isDisabled())
                .collect(Collectors.toList());

            Optional<MongoProcess> process = processes.stream().findFirst();
            if (!process.isPresent()) {
                throw new MongoOpsReplicaSetNotFound(
                    String.format("Unable to get process hosts for replica set '%s' in group '%s", name, groupId) );
            }
            int authSchemaVersion = process.get().getAuthSchemaVersion();

            List<MongoCredential> credentials = new ArrayList<>();
            switch (authSchemaVersion) {
                case 3:
                    credentials.add( MongoCredential
                        .createMongoCRCredential(this.dbAdminUser, "admin", this.dbAdminPassword.toCharArray()) );
                    break;
                case 5:
                    credentials.add(MongoCredential
                        .createScramSha1Credential(this.dbAdminUser, "admin", this.dbAdminPassword.toCharArray()) );
                    break;
                default:
                    throw new MongoOpsException( String.format(
                        "Unrecognized authentication schema version for replica set '%s' in group '%s'",
                        name, groupId ) );
            }

            List<ServerAddress> replicaSet = processes.stream()
                .map(p -> new ServerAddress(p.getHostname(), this.mongodPort))
                .collect(Collectors.toList());

            return new MongoClient(replicaSet, credentials);

        } catch (RuntimeException e) {
            throw new MongoOpsException(e.getMessage(), e.getCause());
        }
    }

    @SuppressWarnings("unchecked")
    private void updateReplicaSet(
        AutomationConfig automationConfig,
        String name,
        String groupId,
        String pattern,
        String dbVersion,
        int authSchemaVersion,
        int nodeCount )
        throws MongoOpsException {

        // Read args2_6 template
        Args args2_6;
        try {
            args2_6 = new ObjectMapper().readValue(new ClassPathResource(this.args2_6_template).getInputStream(), Args.class);
        } catch (Throwable t) {
            throw new MongoOpsDeploymentException(t,
                "Error parsing args2_6 template at '%s'.", this.args2_6_template);
        }

        // Retrieve processes from automation config
        // and extract any processes already deployed
        // if any.

        List<MongoProcess> deployedProcesses = automationConfig.getProcesses(name);

        int deployedSize = deployedProcesses.size();
        if (deployedSize > nodeCount) {
            // Scale down the deployment
            this.removeReplicaSetAgents(automationConfig, name, groupId, deployedSize - nodeCount);
            deployedSize = nodeCount;
        }

        Map<String, Collection<MongoProcess>> processAzMap =
            this.mapObjectsToAZs(deployedProcesses, (MongoProcess p) -> p.getHostname());

        // Retrieve all agents matching given pattern
        // and separate them into their respective azs

        AgentCollection agentCollection = this.clientService.getAgents(groupId, Agent.Type.AUTOMATION);
        Collection<Agent> agents = agentCollection.getResults()
            .stream().sorted((a1, a2) -> a1.getHostname().compareToIgnoreCase(a2.getHostname()))
            .filter(a -> {

                if (a.getStateName().equals("NO_PROCESSES")) {
                    String p = pattern;
                    String h = a.getHostname();
                    if (agentRegistry != null) {
                        String t = agentRegistry.getServiceType(h);
                        if (t != null && t.matches(p)) {
                            return true;
                        }
                    }
                    return h.matches(p);
                }
                return false;
            })
            .collect(Collectors.toList());

        int numAvailableAgents = agents.size();
        if (numAvailableAgents <= this.alertRemainingNodes) {
            this.emailAlertService.sendMessage("alert.moreagents.subject", "alert.moreagents.message1", numAvailableAgents);
        }

        if (numAvailableAgents < (nodeCount - deployedSize)) {

            this.emailAlertService.sendMessage("alert.moreagents.subject", "alert.moreagents.message2", numAvailableAgents);

            throw new MongoOpsDeploymentException(
                "Unable to create replica set '%s' with '%d' nodes as only '%d' nodes are available.",
                name, nodeCount, agents.size() );
        }

        Map<String, Collection<Agent>> agentsByAZ =
            this.mapObjectsToAZs(agents, (Agent a) -> a.getHostname());

        // Determine all AZs of existing processes and available agents
        // are distributed across and sort in ascending order of number
        // of nodes in each AZ. We will pick nodes from each AZ starting
        // with AZ having most nodes first.

        Set<String> azSet = new HashSet<>(agentsByAZ.keySet());
        azSet.addAll(processAzMap.keySet());

        List<String> azs =  azSet.stream()
            .sorted((az1, az2) -> {

                int len1 = (agentsByAZ.containsKey(az1) ? agentsByAZ.get(az1).size() : 0) +
                    (processAzMap.containsKey(az1) ? processAzMap.get(az1).size() : 0);
                int len2 = (agentsByAZ.containsKey(az2) ? agentsByAZ.get(az2).size() : 0) +
                    (processAzMap.containsKey(az2) ? processAzMap.get(az2).size() : 0);

                return (len1 < len2 ? 1 : len1 > len2 ? -1 : 0);
            })
            .collect(Collectors.toList());

        Map<String, Iterator<Agent>> agentsIterator = agentsByAZ.keySet().stream()
            .collect(Collectors.toMap(az -> az, az -> agentsByAZ.get(az).iterator()));
        Map<String, Iterator<MongoProcess>> processesIterator = processAzMap.keySet().stream()
            .collect(Collectors.toMap(az -> az, az -> processAzMap.get(az).iterator()));

        ReplicaSet replicaSet = automationConfig.getReplicaSet(name);
        List<ReplicaSetMember> members = replicaSet.getMembers();

        int numAzs = azs.size();
        int azIndex = 0;
        int nodeIndex = 0;

        while (nodeIndex < nodeCount) {

            String az = azs.get(azIndex);

            MongoProcess process;

            if (nodeIndex < deployedSize) {

                // Retrieve deployed node to be updated
                process = processesIterator.get(az).next();

            } else {

                // Add new node to the deployment
                Iterator<Agent> agentIterator = agentsIterator.get(az);
                if (!agentIterator.hasNext()) {
                    throw new MongoOpsDeploymentException("Unable to select a candidate agent for the replica set.");
                }
                Agent agent = agentIterator.next();

                process = new MongoProcess();
                String processName = String.format("%s_%d", name, nodeIndex);

                process.setName(processName);
                process.setProcessType("mongod");
                process.setHostname(agent.getHostname());
                automationConfig.addProcess(process);

                ReplicaSetMember member = new ReplicaSetMember();
                members.add(member);

                member.set_id(nodeIndex);
                member.setHost(processName);
                member.setPriority(1);
                member.setVotes(1);
                member.setSlaveDelay(0);
                member.setHidden(false);
                member.setArbiterOnly(false);
            }

            process.setVersion(dbVersion);
            process.setAuthSchemaVersion(authSchemaVersion);
            process.setArgs2_6(args2_6);

            args2_6.put("net.port", this.mongodPort);
            args2_6.put("storage.dbPath", String.format("%s/%s", this.mongodDataPath, name));
            args2_6.put("systemLog.path", String.format("%s/%s/mongodb.log", this.mongodDataPath, name));
            args2_6.put("replication.replSetName", name);

            if (args2_6.containsKey("auditLog")) {
                args2_6.put("auditLog.path", String.format("%s/audit/auditLog-%s.bson", this.mongodDataPath, name));
            }

            ++nodeIndex;

            if (++azIndex == numAzs) {
                azIndex = 0;
            }
        }
    }

    private void removeReplicaSetAgents(
        AutomationConfig automationConfig,
        String name,
        String groupId,
        int numNodes )
        throws MongoOpsException {

        // Disable processes of replica set to be removed
        List<MongoProcess> processes = automationConfig.getProcesses(name);

        int deployedSize = processes.size();
        if (numNodes == -1) {
            numNodes = deployedSize;
        }

        processes.stream()
            .sorted((p1, p2) -> p1.getName().compareTo(p2.getName()))
            .collect(Collectors.toList())
            .subList(deployedSize - numNodes, deployedSize).stream()
            .forEach(p -> {
                p.setDisabled(true);
            });

        log.debug(String.format("Disabling processes in replica set '%s'.", name));
        this.sendAutomationConfig(name, groupId, automationConfig);
        this.waitForConvergence(groupId, 60);

        // Remove disabled processes and associated replica set members
        ReplicaSet replicaSet = automationConfig.getReplicaSet(name);
        automationConfig.setProcesses(automationConfig.getProcesses().stream()
            .filter(p -> {
                if (p.isDisabled()) {
                    replicaSet.removeMember(p.getName());
                    return false;
                } else {
                    return true;
                }
            }).collect(Collectors.toList()) );

        // Remove empty replica sets
        automationConfig.setReplicaSets( automationConfig.getReplicaSets().stream()
            .filter(r -> !r.getMembers().isEmpty()).collect(Collectors.toList()) );
    }

    private void sendAutomationConfig(
        String name,
        String groupId,
        AutomationConfig automationConfig)
        throws MongoOpsException {

        if (log.isDebugEnabled()) {

            try {
                StringWriter out = new StringWriter();
                new ObjectMapper().writeValue(out, automationConfig);

                log.debug(
                    String.format("Automation config document with updates for new replica set '%s':\n\t%s",
                    name, out.toString()) );

            } catch (Throwable t) {

                log.error(
                    String.format("Unable to dump automation config document to log: %s",
                    t.getMessage()), t );
            }
        }

        clientService.sendAutomationConfig(groupId, automationConfig);
    }

    private void waitForConvergence(String groupId, long timeout)
        throws MongoOpsException {

        // Workaround for issue in MongoOps versions prior to 2.0 where
        // dead hosts may be returned in the automation status response
        // each having an old goal status. Make sure that when MongoOps
        // has been upgraded this code is removed and the unit test
        // mocks updated.
        AutomationConfig automationConfig = this.clientService.getAutomationConfig(groupId);
        Set<String> hosts = automationConfig.getHosts();

        long timeoutAt = System.currentTimeMillis() + timeout * 1000;
        while (System.currentTimeMillis() < timeoutAt) {

            AutomationStatus status = this.clientService.getAutomationStatus(groupId);
            if (status.isConverged(hosts)) {
                return;
            }

            try {
                Thread.yield();
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new MongoOpsException("Interrupted while waiting for automation convergence", e);
            }
        }

        throw new MongoOpsTimeoutException("Waiting for convergence of goal timed out after %d seconds.", timeout);
    }

    private <T> Map<String, Collection<T>> mapObjectsToAZs(
        Collection<T> objects,
        Hostname<T> hostname)
        throws MongoOpsException {

        Map<String, Collection<T>> objectsByAZ = new HashMap<>();

        // Distribute AZ nodes into multiple iterable collections
        // so nodes can be picked from each to create an even
        // distribution of nodes across zones.

        Iterator<T> i = objects.iterator();
        while (i.hasNext()) {

            T object = i.next();
            String h;
            try {
                h = hostname.get(object);
            } catch (Exception e) {
                throw new MongoOpsException(e, "Exception retrieving string to match AZ.");
            }

            String az = null;
            if (this.agentRegistry != null) {
                az = this.agentRegistry.getAvailabilityZone(h);
            }
            if (az == null && this.nodeAzPattern != null) {
                Matcher m = this.nodeAzPattern.matcher(h);
                if (m.find()) {
                    az = m.group(1);
                }
            }
            if (az == null) {
                az = "_default";
            }

            Collection<T> azObjects = objectsByAZ.get(az);
            if (azObjects == null) {
                azObjects = new ArrayList<>();
                objectsByAZ.put(az, azObjects);
            }
            azObjects.add(object);
        }

        return objectsByAZ;
    }

    private interface Hostname<T> {
        String get(T object);
    }
}
