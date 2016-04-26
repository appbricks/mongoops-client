package org.mongoops.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.*;
import java.util.stream.Collectors;

@JsonIgnoreProperties( ignoreUnknown = true, value = {
    "hosts",
    "replicaSet" } )
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AutomationConfig {

    private Args auth;
    private Args options;
    private String version;
    private List<MonitoringVersion> monitoringVersions;
    private List<BackupVersion> backupVersions;
    private List<MongoProcess> processes;
    private List<ReplicaSet> replicaSets;
    private List<Sharding> sharding;
    private List<MongoDbVersion> mongoDbVersions;

    private Map<String, List<MongoProcess>> processesByName = new HashMap<>();

    public Args getAuth() {
        return auth;
    }

    public void setAuth(Args auth) {
        this.auth = auth;
    }

    public Args getOptions() {
        return options;
    }

    public void setOptions(Args options) {
        this.options = options;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<MonitoringVersion> getMonitoringVersions() {
        return monitoringVersions;
    }

    public void setMonitoringVersions(List<MonitoringVersion> monitoringVersions) {
        this.monitoringVersions = monitoringVersions;
    }

    public List<BackupVersion> getBackupVersions() {
        return backupVersions;
    }

    public void setBackupVersions(List<BackupVersion> backupVersions) {
        this.backupVersions = backupVersions;
    }

    public List<MongoProcess> getProcesses() {

        if (processes == null) {
            processes = new ArrayList<>();
        }
        return processes;
    }

    public List<MongoProcess> getProcesses(String name) {

        if (processes == null) {
            processes = new ArrayList<>();
        }

        List<MongoProcess> processList;
        if (this.processesByName.containsKey(name)) {
            processList = this.processesByName.get(name);
        } else {
            processList = new ArrayList<>();
            processesByName.put(name, processList);
        }
        return processList;
    }

    public void setProcesses(List<MongoProcess> processes) {

        processes.stream()
            .forEach(p -> {

                String processName = p.getName();
                String name = processName.substring(0, p.getName().lastIndexOf('_'));

                List<MongoProcess> processList = processesByName.get(name);
                if (processList == null) {
                    processList = new ArrayList<>();
                    processesByName.put(name, processList);
                }
                processList.add(p);
            });

        this.processes = processes;
    }

    public void addProcess(MongoProcess process) {

        String processName = process.getName();
        String name = processName.substring(0, process.getName().lastIndexOf('_'));

        List<MongoProcess> processList = processesByName.get(name);

        processList.add(process);
        this.processes.add(process);
    }

    public Set<String> getHosts() {
        return processes.stream()
           .map(p -> p.getHostname()).collect(Collectors.toSet());
    }

    public List<ReplicaSet> getReplicaSets() {
        return replicaSets;
    }

    public ReplicaSet getReplicaSet(String name) {

        ReplicaSet replicaSet = null;

        if (replicaSets != null) {

            Optional<ReplicaSet> o = replicaSets
                .stream().filter(rs -> rs.get_id().equals(name)).findFirst();

            replicaSet = (o.isPresent() ? o.get() : null);

        } else {
            replicaSets = new ArrayList<>();
        }

        if (replicaSet == null) {
            replicaSet = new ReplicaSet();
            replicaSet.set_id(name);
            replicaSets.add(replicaSet);
        }
        return replicaSet;
    }

    public void setReplicaSets(List<ReplicaSet> replicaSets) {
        this.replicaSets = replicaSets;
    }

    public List<Sharding> getSharding() {
        return sharding;
    }

    public void setSharding(List<Sharding> sharding) {
        this.sharding = sharding;
    }

    public List<MongoDbVersion> getMongoDbVersions() {
        return mongoDbVersions;
    }

    public void setMongoDbVersions(List<MongoDbVersion> mongoDbVersion) {
        this.mongoDbVersions = mongoDbVersion;
    }
}
