package org.mongoops.client.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.DigestScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Configuration
public class MongoOpsClientConfig {

    private static final Log log = LogFactory.getLog(MongoOpsClientConfig.class);

    @Autowired
    private MongoOpsHostConfig hostConfig;

    private HttpHost httpHost;

    @Bean
    public RestTemplate mongoOpsRestTemplate() {

        // Implementation adapted from http://www.baeldung.com/resttemplate-digest-authentication

        CredentialsProvider provider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(
            this.hostConfig.getApiUser(), this.hostConfig.getApiKey() );
        provider.setCredentials(AuthScope.ANY, credentials);

        final CloseableHttpClient client = HttpClientBuilder.create()
            .setDefaultCredentialsProvider(provider).useSystemProperties().build();

        return new RestTemplate(new HttpComponentsClientHttpRequestFactory(client) {

            @Override
            protected HttpContext createHttpContext(final HttpMethod httpMethod, final URI uri) {

                // Create AuthCache instance
                AuthCache authCache = new BasicAuthCache();
                // Generate DIGEST scheme object, initialize it and add it to the local auth cache
                DigestScheme digestAuth = new DigestScheme();
                // If we already know the realm name
                digestAuth.overrideParamter("realm", MongoOpsClientConfig.this.hostConfig.getApiRealm());

                // digestAuth.overrideParamter("nonce", "MTM3NTU2OTU4MDAwNzoyYWI5YTQ5MTlhNzc5N2UxMGM5M2Y5M2ViOTc4ZmVhNg==");
                authCache.put(MongoOpsClientConfig.this.mongoOpsHost(), digestAuth);

                // Add AuthCache to the execution context
                BasicHttpContext context = new BasicHttpContext();
                context.setAttribute(HttpClientContext.AUTH_CACHE, authCache);
                return context;
            }
        });
    }

    @Bean
    public HttpHost mongoOpsHost() {
        if (this.httpHost == null) {

            log.debug(this.hostConfig.toString());

            this.httpHost = new HttpHost(
                this.hostConfig.getServer(), this.hostConfig.getPort(), this.hostConfig.isSecure() ? "https" : "http");
        }
        return this.httpHost;
    }

    @Bean
    public String mongoOpsApiPath() {
        return this.hostConfig.getApiPath();
    }

    @Bean
    public String mongoOpsGroupId() {
        return this.hostConfig.getGroupId();
    }

    @Bean
    public String mongoOpsDbAdminUser() {
        return this.hostConfig.getDbAdminUser();
    }

    @Bean
    public String mongoOpsDbAdminPassword() {
        return this.hostConfig.getDbAdminPassword();
    }
}
