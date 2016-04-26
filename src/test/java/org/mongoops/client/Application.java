package org.mongoops.client;

import org.mongoops.client.service.MongoAgentRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;

@SpringBootApplication(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
@EnableScheduling
public class Application
    extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

    public static void main(String[] args) {
        final ApplicationContext applicationContext = SpringApplication.run(Application.class, args);
    }

    public MongoAgentRegistry agentRegistry() {

        return new MongoAgentRegistry() {
            @Override
            public String getAvailabilityZone(String hostname) {
                return null;
            }

            @Override
            public String getServiceType(String hostname) {
                return null;
            }

            @Override
            public List<String> getAvailabilityZones() {
                return null;
            }

            @Override
            public List<String> getServiceTypes() {
                return null;
            }
        };
    }
}