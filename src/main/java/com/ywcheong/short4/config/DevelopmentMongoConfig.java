package com.ywcheong.short4.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("dev")
@Configuration
@Slf4j
public class DevelopmentMongoConfig {

    @Value("${short4.mongodb.container-ip}")
    private String containerIp;

    @Getter
    @Value("${short4.mongodb.container-port}")
    private int containerPort;

    @Getter
    @Value("${short4.mongodb.container-database}")
    private String containerDatabase;

    @Bean
    @Primary
    public MongoProperties developmentMongoProperties() {
        MongoProperties properties = new MongoProperties();

        properties.setHost(containerIp);
        properties.setPort(containerPort);
        properties.setDatabase(containerDatabase);

        log.info("Development MongoDB Config :: properties set :: host [{}] port [{}] database [{}]",
                properties.getHost(),
                properties.getPort(),
                properties.getDatabase());

        return properties;
    }
}
