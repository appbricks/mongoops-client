package org.mongoops.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LogRotate {

    private int sizeThresholdMB;
    private int timeThresholdHrs;

    public int getSizeThresholdMB() {
        return sizeThresholdMB;
    }

    public void setSizeThresholdMB(int sizeThresholdMB) {
        this.sizeThresholdMB = sizeThresholdMB;
    }

    public int getTimeThresholdHrs() {
        return timeThresholdHrs;
    }

    public void setTimeThresholdHrs(int timeThresholdHrs) {
        this.timeThresholdHrs = timeThresholdHrs;
    }
}
