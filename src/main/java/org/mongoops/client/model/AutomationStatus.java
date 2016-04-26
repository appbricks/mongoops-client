package org.mongoops.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AutomationStatus {

    private String goalVersion;
    private List<MongoProcess> processes;

    public String getGoalVersion() {
        return goalVersion;
    }

    public void setGoalVersion(String goalVersion) {
        this.goalVersion = goalVersion;
    }

    public List<MongoProcess> getProcesses() {
        return processes;
    }

    public void setProcesses(List<MongoProcess> processes) {
        this.processes = processes;
    }

    public boolean isConverged(Set<String> hosts) {

        return processes.size()==0 || processes
            .stream()
            .filter(p -> hosts.contains(p.getHostname()))
            .map(p -> p.getLastGoalVersionAchieved().equals(this.goalVersion))
            .reduce(
                true,
                (result, achieved) -> result && achieved);
    }
}
