package org.mongoops.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Agent {

    public static final class Type
        extends StringEnum {

        public static final Type MONITORING = new Type("MONITORING");
        public static final Type BACKUP = new Type("BACKUP");
        public static final Type AUTOMATION = new Type("AUTOMATION");

        private Type(String type) {
            super(type);
        }
    }

    private String typeName;
    private String hostname;
    private int confCount;
    private Date lastConf;
    private String stateName;
    private int pingCount;
    private boolean isManaged;
    private Date lastPing;
    private String tag;

    public boolean isType(Type type) {
        return this.typeName.equals(type.toString());
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getConfCount() {
        return confCount;
    }

    public void setConfCount(int confCount) {
        this.confCount = confCount;
    }

    public Date getLastConf() {
        return lastConf;
    }

    public void setLastConf(Date lastConf) {
        this.lastConf = lastConf;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public int getPingCount() {
        return pingCount;
    }

    public void setPingCount(int pingCount) {
        this.pingCount = pingCount;
    }

    public boolean isManaged() {
        return isManaged;
    }

    public void setIsManaged(boolean isManaged) {
        this.isManaged = isManaged;
    }

    public Date getLastPing() {
        return lastPing;
    }

    public void setLastPing(Date lastPing) {
        this.lastPing = lastPing;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
