package org.mongoops.client.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.Cloud;
import org.springframework.cloud.CloudFactory;
import org.springframework.cloud.config.java.ServiceScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@ServiceScan
@Profile({ "cloud_development", "cloud_test", "cloud_production" })
public class CloudConfiguration {

    private static final Log log = LogFactory.getLog(CloudConfiguration.class);

    @Value("${mongodb.om.apiPath:/api/public/v1.0}")
    private String apiPath;

    @Value("${mongodb.om.apiRealm:MMS Public API}")
    private String apiRealm;

    @Bean
    public MongoOpsHostConfig mongoOpsHostConfig() {

        CloudFactory cloudFactory = new CloudFactory();
        Cloud cloud = cloudFactory.getCloud();

        MongoOpsHostConfig hostConfig = (MongoOpsHostConfig) cloud.getServiceInfo(MongoOpsHostConfig.NAME);
        hostConfig.setApiPathIfNull(this.apiPath);
        hostConfig.setApiRealmIfNull(this.apiRealm);
        return hostConfig;
    }
}
