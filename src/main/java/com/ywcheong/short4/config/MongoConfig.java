package com.ywcheong.short4.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Configuration
public class MongoConfig {
    @Value("${spring.data.mongodb.username}")
    private String username;

    @Value("${spring.data.mongodb.port}")
    private int port;

    @Value("${spring.data.mongodb.database}")
    private String database;

    @Bean
    @Primary
    public MongoProperties mongoProperties() throws IOException {
        // mongodb://short4_user:[[mongodb_user_password]]@mongodb:27017/short4
        String password = new String(Files.readAllBytes(Paths.get("/run/secrets/mongodb_user_password")));

        MongoProperties properties = new MongoProperties();
        properties.setHost("mongodb");
        properties.setPort(port);
        properties.setDatabase(database);
        properties.setUsername(username);
        properties.setPassword(password.toCharArray());
        return properties;
    }
}
