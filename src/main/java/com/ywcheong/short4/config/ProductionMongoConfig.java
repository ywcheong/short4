package com.ywcheong.short4.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Profile("prod")
@Configuration
@Slf4j
public class ProductionMongoConfig {

    @Value("${spring.data.mongodb.host}")
    private String host;

    @Value("${spring.data.mongodb.port}")
    private int port;

    @Value("${spring.data.mongodb.username}")
    private String username;

    @Value("${spring.data.mongodb.database}")
    private String database;

    @Bean
    @Primary
    public MongoProperties productionMongoProperties() throws IOException {
        // mongodb://short4_user:[[mongodb_user_password]]@mongodb:27017/short4
        MongoProperties properties = new MongoProperties();

        properties.setHost(host);
        properties.setPort(port);
        properties.setUsername(username);
        properties.setDatabase(database);

        String password = new String(Files.readAllBytes(Paths.get("/run/secrets/mongodb_user_password")));
        properties.setPassword(password.toCharArray());

        log.info("Production MongoDB Config :: properties set :: host [{}] port [{}] database [{}] username [{}]",
                properties.getHost(),
                properties.getPort(),
                properties.getDatabase(),
                properties.getUsername());

        return properties;
    }
}
