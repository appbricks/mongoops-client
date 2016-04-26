package org.mongoops.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReplicaSetMember {

    private int _id;
    private String host;
    private int priority;
    private int votes;
    private int slaveDelay;
    private boolean hidden;
    private boolean arbiterOnly;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public int getSlaveDelay() {
        return slaveDelay;
    }

    public void setSlaveDelay(int slaveDelay) {
        this.slaveDelay = slaveDelay;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean isArbiterOnly() {
        return arbiterOnly;
    }

    public void setArbiterOnly(boolean arbiterOnly) {
        this.arbiterOnly = arbiterOnly;
    }
}
