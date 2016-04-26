package org.mongoops.client.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;
import org.mongoops.client.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.InputStreamReader;
import java.io.StringWriter;

/**
 * MongoDB Enterprise Ops Manager client service
 */
@Service
public class MongoOpsClientService {

    private static final Log log = LogFactory.getLog(MongoOpsClientService.class);

    private String apiPath;

    private HttpHost mongoOpsHost;
    private RestTemplate mongoOpsRestTemplate;

    @Autowired
    public MongoOpsClientService(
        HttpHost mongoOpsHost,
        String mongoOpsApiPath,
        RestTemplate mongoOpsRestTemplate ) {

        this.mongoOpsHost = mongoOpsHost;
        this.apiPath = mongoOpsApiPath;
        this.mongoOpsRestTemplate = mongoOpsRestTemplate;

        if (log.isDebugEnabled()) {

            this.mongoOpsRestTemplate.setRequestFactory(
                new BufferingClientHttpRequestFactory(this.mongoOpsRestTemplate.getRequestFactory()) );

            this.mongoOpsRestTemplate.getInterceptors().add(
                (HttpRequest request, byte[] body, ClientHttpRequestExecution execution) -> {

                    if (body.length > 0) {
                        log.debug(String.format("Body of request to -> %s:\n\t%s", request.getURI(), new String(body)));
                    }
                    ClientHttpResponse response = execution.execute(request, body);

                    StringWriter logOut = new StringWriter();
                    InputStreamReader responseBody = new InputStreamReader(response.getBody());

                    char[] buffer = new char[128];
                    int length;

                    while ((length = responseBody.read(buffer)) != -1) {
                        logOut.write(buffer, 0, length);
                    }

                    log.debug(String.format("Body of response from -> %s:\n\t%s", request.getURI(), logOut.toString()));

                    return response;
                } );
        }
    }

    public MongoOpsClientService(HttpHost mongoOpsHost, String mongoOpsApiPath) {
        this.mongoOpsHost = mongoOpsHost;
        this.apiPath = mongoOpsApiPath;
    }

    protected RestTemplate getMongoOpsRestTemplate() {
        return mongoOpsRestTemplate;
    }

    protected void setMongoOpsRestTemplate(RestTemplate mongoOpsRestTemplate) {
        this.mongoOpsRestTemplate = mongoOpsRestTemplate;
    }

    public User getUserByName(String username) {

        String url = this.resourceUrl("users", "byName", username);
        return this.mongoOpsRestTemplate.getForObject(url, User.class);
    }

    public User createUser(User user) {

        String url = this.resourceUrl("users");
        return this.mongoOpsRestTemplate.postForObject(url, user, User.class);
    }

    public User updateUser(User user) {

        String url = this.resourceUrl("users", user.getId());
        ResponseEntity<User> resp = this.mongoOpsRestTemplate.exchange(
            url, HttpMethod.PATCH, new HttpEntity<User>(user), User.class);
        return resp.getBody();
    }

    public void deleteGroupUser(String groupId, String userid) {

        String url = this.groupResourceUrl(groupId, "users", userid);
        this.mongoOpsRestTemplate.delete(url);
    }

    public AgentCollection getAgents(String groupId) {

        return this.getAgents(groupId, null);
    }

    public AgentCollection getAgents(String groupId, Agent.Type type) {

        String url = ( type==null
            ? this.groupResourceUrl(groupId, "agents")
            : this.groupResourceUrl(groupId, "agents", type.toString()) );

        return this.mongoOpsRestTemplate.getForObject(url, AgentCollection.class);
    }

    public AutomationStatus getAutomationStatus(String groupId) {

        String url = this.groupResourceUrl(groupId, "automationStatus");
        return this.mongoOpsRestTemplate.getForObject(url, AutomationStatus.class);
    }

    public AutomationConfig getAutomationConfig(String groupId) {

        String url = this.groupResourceUrl(groupId, "automationConfig");
        return this.mongoOpsRestTemplate.getForObject(url, AutomationConfig.class);
    }

    public void sendAutomationConfig(String groupId, AutomationConfig automationConfig) {

        String url = this.groupResourceUrl(groupId, "automationConfig");
        this.mongoOpsRestTemplate.put(url, automationConfig);
    }

    protected String groupResourceUrl(String groupId, String... pathElements) {

        StringBuilder url = new StringBuilder(this.mongoOpsHost.toURI());
        url.append(this.apiPath);
        url.append("/groups/");
        url.append(groupId);

        for (String pathElement : pathElements) {
            url.append('/').append(pathElement);
        }
        return url.toString();
    }

    protected String resourceUrl(String... pathElements) {

        StringBuilder url = new StringBuilder(this.mongoOpsHost.toURI());
        url.append(this.apiPath);

        for (String pathElement : pathElements) {
            url.append('/').append(pathElement);
        }
        return url.toString();
    }
}
