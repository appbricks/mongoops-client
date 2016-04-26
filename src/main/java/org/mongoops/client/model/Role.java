package org.mongoops.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Role {


    public static final String ROLE_GLOBAL_AUTOMATION_ADMIN = "GLOBAL_AUTOMATION_ADMIN";
    public static final String ROLE_GLOBAL_BACKUP_ADMIN = "GLOBAL_BACKUP_ADMIN";
    public static final String ROLE_GLOBAL_MONITORING_ADMIN = "GLOBAL_MONITORING_ADMIN";
    public static final String ROLE_GLOBAL_OWNER = "GLOBAL_OWNER";
    public static final String ROLE_GLOBAL_READ_ONLY = "GLOBAL_READ_ONLY";
    public static final String ROLE_GLOBAL_USER_ADMIN = "GLOBAL_USER_ADMIN";
    public static final String ROLE_GROUP_AUTOMATION_ADMIN = "GROUP_AUTOMATION_ADMIN";
    public static final String ROLE_GROUP_BACKUP_ADMIN = "GROUP_BACKUP_ADMIN";
    public static final String ROLE_GROUP_MONITORING_ADMIN = "GROUP_MONITORING_ADMIN";
    public static final String ROLE_GROUP_OWNER = "GROUP_OWNER";
    public static final String ROLE_GROUP_READ_ONLY = "GROUP_READ_ONLY";
    public static final String ROLE_GROUP_USER_ADMIN = "GROUP_USER_ADMIN";

    private String groupId;
    private String roleName;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
