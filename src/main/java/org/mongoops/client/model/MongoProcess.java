package org.mongoops.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MongoProcess {

    private String name;
    private String processType;
    private String version;
    private String hostname;
    private String cluster;
    private LogRotate logRotate;
    private int authSchemaVersion;

    private Args args2_6;
    private Args args2_4;
    private boolean disabled;

    private String lastGoalVersionAchieved;
    private String[] plan;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProcessType() {
        return processType;
    }

    public void setProcessType(String processType) {
        this.processType = processType;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public LogRotate getLogRotate() {
        return logRotate;
    }

    public void setLogRotate(LogRotate logRotate) {
        this.logRotate = logRotate;
    }

    public int getAuthSchemaVersion() {
        return authSchemaVersion;
    }

    public void setAuthSchemaVersion(int authSchemaVersion) {
        this.authSchemaVersion = authSchemaVersion;
    }

    public Args getArgs2_6() {
        return args2_6;
    }

    public void setArgs2_6(Args args2_6) {
        this.args2_6 = args2_6;
    }

    public Args getArgs2_4() {
        return args2_4;
    }

    public void setArgs2_4(Args args2_4) {
        this.args2_4 = args2_4;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public String getLastGoalVersionAchieved() {
        return lastGoalVersionAchieved;
    }

    public void setLastGoalVersionAchieved(String lastGoalVersionAchieved) {
        this.lastGoalVersionAchieved = lastGoalVersionAchieved;
    }

    public String[] getPlan() {
        return plan;
    }

    public void setPlan(String[] plan) {
        this.plan = plan;
    }

}
