package com.ywcheong.short4.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Files;
import java.nio.file.Paths;

@Configuration
public class MongoConfig {

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @PostConstruct
    public void configureMongoUri() throws Exception {
        String password = new String(Files.readAllBytes(Paths.get("/run/secrets/mongodb_user_password")));
        mongoUri = mongoUri.replace("[[mongodb_user_password]]", password.trim());
    }
}
