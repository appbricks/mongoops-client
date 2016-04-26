package org.mongoops.client.config;

import org.springframework.cloud.service.BaseServiceInfo;

class MongoOpsHostConfig
    extends BaseServiceInfo {

    public static final String NAME = "mongo-ops-service";

    private String server;
    private int port;
    private boolean secure;
    private String apiPath;
    private String apiRealm;
    private String apiUser;
    private String apiKey;
    private String groupId;
    private String dbAdminUser;
    private String dbAdminPassword;

    MongoOpsHostConfig(
        String server, int port, boolean secure,
        String apiPath, String apiRealm,
        String apiUser, String apiKey, String groupId,
        String dbAdminUser, String dbAdminPassword ) {

        super(MongoOpsHostConfig.NAME);

        this.server = server;
        this.port = port;
        this.secure = secure;
        this.apiPath = apiPath;
        this.apiRealm = apiRealm;
        this.apiUser = apiUser;
        this.apiKey = apiKey;
        this.groupId = groupId;
        this.dbAdminUser = dbAdminUser;
        this.dbAdminPassword = dbAdminPassword;
    }

    public String getServer() {
        return server;
    }

    public int getPort() {
        return port;
    }

    public boolean isSecure() {
        return secure;
    }

    public String getApiPath() {
        return apiPath;
    }

    void setApiPathIfNull(String apiPath) {
        if (this.apiPath == null) {
            this.apiPath = apiPath;
        }
    }

    public String getApiRealm() {
        return apiRealm;
    }

    void setApiRealmIfNull(String apiRealm) {
        if (this.apiRealm == null) {
            this.apiRealm = apiRealm;
        }
    }

    public String getApiUser() {
        return apiUser;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getDbAdminUser() {
        return dbAdminUser;
    }

    public String getDbAdminPassword() {
        return dbAdminPassword;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder(this.getId());
        sb.append("[server=").append(this.server).append(", ");
        sb.append("port=").append(this.port).append(", ");
        sb.append("apiPath=").append(this.apiPath).append(", ");
        sb.append("apiRealm=").append(this.apiRealm).append(", ");
        sb.append("isSecure=").append(this.isSecure()).append(", ");
        sb.append("apiUser=").append(this.apiUser).append(", ");
        sb.append("groupId=").append(this.groupId).append(", ");
        sb.append("dbAdminUser=").append(this.dbAdminUser).append("]");

        return sb.toString();
    }
}
