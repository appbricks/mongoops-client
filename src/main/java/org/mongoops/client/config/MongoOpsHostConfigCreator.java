package org.mongoops.client.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cloud.cloudfoundry.CloudFoundryServiceInfoCreator;
import org.springframework.cloud.cloudfoundry.Tags;

import java.util.Map;

public class MongoOpsHostConfigCreator
    extends CloudFoundryServiceInfoCreator<MongoOpsHostConfig> {

    private static final Log log = LogFactory.getLog(MongoOpsHostConfigCreator.class);

    public MongoOpsHostConfigCreator() {
        super(new Tags());
    }

    @Override
    public boolean accept(Map<String, Object> serviceData) {

        String name = (String) serviceData.get("name");
        String label = (String) serviceData.get("label");

        return name.equals(MongoOpsHostConfig.NAME) && label.equals("user-provided");
    }

    @Override
    public MongoOpsHostConfig createServiceInfo(Map<String, Object> serviceData) {

        Map<String, Object> credentials = super.getCredentials(serviceData);

        String host = (String) credentials.get("host");
        String port = (String) credentials.get("port");
        String isSecure = (String) credentials.get("secure");
        String apiPath = (String) credentials.get("api-path");
        String apiRealm = (String) credentials.get("api-realm");
        String apiUser = (String) credentials.get("api-user");
        String apiKey = (String) credentials.get("api-key");
        String groupId = (String) credentials.get("group-id");
        String dbAdminUser = (String) credentials.get("db-admin-user");
        String dbAdminPassword = (String) credentials.get("db-admin-password");

        if ( host == null || port == null ||
            apiUser == null || apiKey == null || groupId == null ||
            dbAdminUser == null || dbAdminPassword == null ) {

            throw new RuntimeException(
                "The following credentials are mandatory for the user-provided service '" + MongoOpsHostConfig.NAME +
                "': host, port, api-user, api-key, group-id, db-admin-user, db-admin-password" );
        }

        MongoOpsHostConfig hostConfig = new MongoOpsHostConfig(
            host, Integer.parseInt(port),
            isSecure != null && isSecure.equalsIgnoreCase("true"),
            apiPath, apiRealm,
            apiUser, apiKey, groupId,
            dbAdminUser, dbAdminPassword );

        log.debug(String.format("Creating service info instance: %s", host.toString()));
        return hostConfig;
    }
}
