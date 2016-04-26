package org.mongoops.client.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({ "default", "test" })
public class LocalConfiguration {

    @Value("${mongodb.om.host}")
    private String server;

    @Value("${mongodb.om.port}")
    private int port;

    @Value("${mongodb.om.secure:false}")
    private boolean secure;

    @Value("${mongodb.om.apiPath:/api/public/v1.0}")
    private String apiPath;

    @Value("${mongodb.om.apiRealm:MMS Public API}")
    private String apiRealm;

    @Value("${mongodb.om.apiUser}")
    private String apiUser;

    @Value("${mongodb.om.apiKey}")
    private String apiKey;

    @Value("${mongodb.om.groupId}")
    private String groupId;

    @Value("${mongodb.om.dbAdminUser}")
    private String dbAdminUser;

    @Value("${mongodb.om.dbAdminPassword}")
    private String dbAdminPassword;

    @Bean
    public MongoOpsHostConfig mongoOpsHostConfig() {

        return new MongoOpsHostConfig(
            this.server, this.port, this.secure,
            this.apiPath, this.apiRealm,
            this.apiUser, this.apiKey, this.groupId,
            this.dbAdminUser, this.dbAdminPassword );
    }
}
